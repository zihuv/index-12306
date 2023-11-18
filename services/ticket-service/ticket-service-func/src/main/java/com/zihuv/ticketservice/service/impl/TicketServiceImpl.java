package com.zihuv.ticketservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.orderservice.feign.OrderFeign;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.ticketservice.common.constant.Index12306Constant;
import com.zihuv.ticketservice.common.enums.TicketChainMarkEnum;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.dto.TicketPurchaseDTO;
import com.zihuv.ticketservice.model.vo.TicketOrderDetailVO;
import com.zihuv.ticketservice.model.entity.Station;
import com.zihuv.ticketservice.model.entity.Ticket;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.model.entity.TrainStation;
import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;
import com.zihuv.ticketservice.model.vo.TicketPurchaseVO;
import com.zihuv.ticketservice.model.vo.TicketPageQueryVO;
import com.zihuv.ticketservice.service.*;
import com.zihuv.ticketservice.mapper.TicketMapper;
import com.zihuv.ticketservice.service.select.TrainSeatTypeSelector;
import com.zihuv.ticketservice.service.tokenbucket.TicketAvailabilityTokenBucket;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.*;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    private final DistributedCache distributedCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderFeign orderFeign;
    private final TrainService trainService;
    private final StationService stationService;
    private final TrainStationService trainStationService;
    private final SeatService seatService;
    private final TrainSeatTypeSelector trainSeatTypeSelector;
    private final AbstractChainContext<TicketPageQueryParam> ticketPageQueryAbstractChainContext;
    private final AbstractChainContext<TicketPurchaseDetailParam> purchaseTicketAbstractChainContext;
    private final TicketAvailabilityTokenBucket ticketAvailabilityTokenBucket;

    @Override
    public List<TicketPageQueryVO> pageTicketQuery(TicketPageQueryParam ticketPageQueryParam) {
        // TODO 不考虑中转的情况，只考虑单程直达
        // 责任链模式：1.各个参数不能为空 2.出发日期不能小于当前日期 3.添加所有地区与车站的缓存，并校验这些名称是否真实存在
        ticketPageQueryAbstractChainContext.handler(TicketChainMarkEnum.TRAIN_QUERY_FILTER.name(), ticketPageQueryParam);

        long stationFromId = Long.parseLong(String.valueOf(this.getStationByStationName(ticketPageQueryParam.getDeparture()).getId()));
        long stationToId = Long.parseLong(String.valueOf(this.getStationByStationName(ticketPageQueryParam.getArrival()).getId()));

        // 使用 redis 对出发站和目标站的列车 id 取交集，获取可以直达的列车 id
        Set<Object> stationIdSet = redisTemplate.opsForSet().intersect(STATION_TRAIN_PASS_SET + stationFromId, STATION_TRAIN_PASS_SET + stationToId);
        if (CollUtil.isEmpty(stationIdSet)) {
            throw new ServiceException("出发地和目的地不存在直达");
        }
        // 校验这些列车是否真的可以从出发站直达目标站
        List<Long> stationIdList = new ArrayList<>();
        for (Object stationId : stationIdSet) {
            // 将 code 转化为名称
            String fromStationName = ticketPageQueryParam.getDeparture();
            String toStationName = ticketPageQueryParam.getArrival();
            // 根据名称查询路线
            List<RouteDTO> routeDTOList = trainStationService.listTrainStationRoute(String.valueOf(stationId), fromStationName, toStationName);
            if (CollUtil.isEmpty(routeDTOList)) {
                throw new ServiceException("该路线的列车无法直达");
            }
            stationIdList.add(Long.parseLong(String.valueOf(stationId)));
        }
        // TODO 查询缓存 直接查出路线
        List<TicketPageQueryVO> ticketPageQueryVOList = new ArrayList<>();
        List<Train> trains = trainService.listByIds(stationIdList);
        for (Train train : trains) {
            List<SeatTypeCountDTO> seatTypeCountDTOList = seatService.listSeatTypeCount(train.getId(), train.getStartStation(), train.getEndStation(), train.getTrainType());

            Duration duration = LocalDateTimeUtil.between(train.getDepartureTime(), train.getArrivalTime());
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes();
            String spendTime = hours + "时" + minutes + "分";

            TicketPageQueryVO ticketPageQueryVO = new TicketPageQueryVO();
            ticketPageQueryVO.setTrainId(train.getId());
            ticketPageQueryVO.setTrainNumber(train.getTrainNumber());
            ticketPageQueryVO.setTrainType(train.getTrainType());
            ticketPageQueryVO.setTrainTag(train.getTrainTag());
            ticketPageQueryVO.setTrainBrand(train.getTrainBrand());
            ticketPageQueryVO.setStartStation(train.getStartStation());
            ticketPageQueryVO.setEndStation(train.getEndStation());
            ticketPageQueryVO.setStartRegion(train.getStartRegion());
            ticketPageQueryVO.setEndRegion(train.getEndRegion());
            ticketPageQueryVO.setDepartureTime(String.valueOf(train.getDepartureTime()));
            ticketPageQueryVO.setArrivalTime(String.valueOf(train.getArrivalTime()));
            ticketPageQueryVO.setSpendTime(spendTime);
            ticketPageQueryVO.setSeatTypeCountDTOList(seatTypeCountDTOList);
            ticketPageQueryVOList.add(ticketPageQueryVO);
        }
        return ticketPageQueryVOList;
    }

    @Override
    public TicketPurchaseVO purchaseTickets(TicketPurchaseDetailParam requestParam) {
        // 责任链模式，验证 1：参数不为空 2：参数是否有效 3：乘客是否重复买票
        purchaseTicketAbstractChainContext.handler(TicketChainMarkEnum.TRAIN_PURCHASE_TICKET_FILTER.name(), requestParam);
        // 从令牌桶中获取买票资格
        // TODO 当座位字母代号已售尽时，是使用随机分配票，还是需要拿到 token 的
        boolean tokenResult = ticketAvailabilityTokenBucket.takeTokenFromBucket(requestParam);
        if (!tokenResult) {
            throw new ServiceException("该座位类型已无余票");
        }
        // 挑选座位
        List<TicketPurchaseDTO> selectSeat = trainSeatTypeSelector.select(requestParam);

        System.out.println(selectSeat);
        // 对座位类型映射

        // 对座位加锁，防止同个座位被超卖

        // 发生异常，回滚令牌桶中的令牌

        Train train = distributedCache.safeGet(
                TRAIN_INFO + requestParam.getTrainId(),
                Train.class,
                () -> trainService.getById(requestParam.getTrainId()),
                Index12306Constant.ADVANCE_TICKET_DAY,
                TimeUnit.DAYS);

        List<TicketOrderDetailVO> ticketOrderDetailVOList = new ArrayList<>();
        for (TicketPurchaseDTO ticketPurchaseDTO : selectSeat) {
            // 封装 VO
            TicketOrderDetailVO ticketOrderDetailVO = new TicketOrderDetailVO();
            ticketOrderDetailVO.setSeatType(ticketPurchaseDTO.getSeatType());
            ticketOrderDetailVO.setCarriageNumber(ticketPurchaseDTO.getCarriageNumber());
            ticketOrderDetailVO.setSeatNumber(ticketPurchaseDTO.getSeatNumber());
            ticketOrderDetailVO.setRealName(ticketPurchaseDTO.getRealName());
            ticketOrderDetailVO.setIdType(ticketPurchaseDTO.getIdType());
            ticketOrderDetailVO.setIdCard(ticketPurchaseDTO.getIdCard());
            // TODO 成人票/学生票 涉及算费
            // ticketOrderDetailVO.setTicketType();
            // ticketOrderDetailVO.setAmount();
            ticketOrderDetailVOList.add(ticketOrderDetailVO);

            // 封装订单
            TicketOrderCreateParam ticketOrderCreateParam = new TicketOrderCreateParam();
            ticketOrderCreateParam.setUserId(Long.parseLong(ticketPurchaseDTO.getPassengerId()));
            ticketOrderCreateParam.setRealName(ticketPurchaseDTO.getRealName());
            ticketOrderCreateParam.setTrainId(Long.parseLong(requestParam.getTrainId()));
            ticketOrderCreateParam.setTrainNumber(ticketPurchaseDTO.getCarriageNumber());
            ticketOrderCreateParam.setDeparture(requestParam.getDeparture());
            ticketOrderCreateParam.setArrival(requestParam.getArrival());
            ticketOrderCreateParam.setDepartureTime(train.getDepartureTime());
            ticketOrderCreateParam.setArrivalTime(train.getArrivalTime());
            // 发送创建订单请求
            orderFeign.createOrder(ticketOrderCreateParam);
        }

        // TODO 一个订单代表多个乘车人的订单？
        TicketPurchaseVO ticketPurchaseVO = new TicketPurchaseVO();
        ticketPurchaseVO.setOrderSn(null);
        ticketPurchaseVO.setTicketOrderDetails(ticketOrderDetailVOList);

        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public TicketPurchaseVO executePurchaseTickets(TicketPurchaseDetailParam requestParam) {
        // 记录车票订单
        List<TicketOrderDetailVO> ticketOrderDetailResults = new ArrayList<>();
        // 查询列车信息
        String trainId = requestParam.getTrainId();
        Train train = distributedCache.safeGet(
                TRAIN_INFO + trainId,
                Train.class,
                () -> trainService.getById(trainId),
                Index12306Constant.ADVANCE_TICKET_DAY,
                TimeUnit.DAYS);
        // 挑选座位

        // 创建订单

        // 封装参数
//        TicketPurchaseVO ticketPurchaseVO = new TicketPurchaseVO();
//        ticketPurchaseVO.setOrderSn();
//        ticketPurchaseVO.setTicketOrderDetails();
//
//        TicketOrderDetailVO ticketOrderDetailVO = new TicketOrderDetailVO();
//        ticketOrderDetailVO.setSeatType();
//        ticketOrderDetailVO.setCarriageNumber();
//        ticketOrderDetailVO.setSeatNumber();
//        ticketOrderDetailVO.setRealName();
//        ticketOrderDetailVO.setIdType();
//        ticketOrderDetailVO.setIdCard();
//        ticketOrderDetailVO.setTicketType();
//        ticketOrderDetailVO.setAmount();


        return null;
    }

    /**
     * 初始化缓存
     * TODO 后续修改为任务调度更新缓存
     */
    @PostConstruct
    public void initCache() {
        // 添加缓存：车站 id {1} - 列车 id {1,2,3,4}
        LambdaQueryWrapper<TrainStation> lqw = new LambdaQueryWrapper<>();
        lqw.select(TrainStation::getTrainId, TrainStation::getStationId);
        List<TrainStation> trainStationList = trainStationService.list(lqw);
        for (TrainStation trainStation : trainStationList) {
            if (trainStation.getTrainId() != null && trainStation.getStationId() != null) {
                redisTemplate.opsForSet().add(STATION_TRAIN_PASS_SET + trainStation.getStationId(), trainStation.getTrainId());
            }

        }

    }

    /**
     * 查询车站并添加其缓存
     *
     * @param stationName 车站名称
     * @return 车站
     */
    private Station getStationByStationName(String stationName) {
        Station station = distributedCache.get(TRAIN_STATION_INFO + stationName, Station.class);
        if (station == null) {
            LambdaQueryWrapper<Station> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Station::getName, stationName);
            station = this.stationService.getOne(lqw);
            if (station == null) {
                throw new ServiceException("该车站不存在");
            }
            distributedCache.put(TRAIN_STATION_INFO + stationName, station);
        }
        return station;
    }
}




