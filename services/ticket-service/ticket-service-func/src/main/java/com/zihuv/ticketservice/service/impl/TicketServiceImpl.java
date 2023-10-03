package com.zihuv.ticketservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.ticketservice.common.enums.TicketChainMarkEnum;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.entity.Station;
import com.zihuv.ticketservice.model.entity.Ticket;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.model.entity.TrainStation;
import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;
import com.zihuv.ticketservice.model.vo.TicketOrderDetailVO;
import com.zihuv.ticketservice.model.vo.TicketPageQueryVO;
import com.zihuv.ticketservice.service.*;
import com.zihuv.ticketservice.mapper.TicketMapper;
import com.zihuv.ticketservice.tokenbucket.TicketAvailabilityTokenBucket;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.*;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    private final DistributedCache distributedCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TrainService trainService;
    private final StationService stationService;
    private final TrainStationService trainStationService;
    private final SeatService seatService;
    private final AbstractChainContext<TicketPageQueryParam> ticketPageQueryAbstractChainContext;
    private final AbstractChainContext<PurchaseTicketDetailParam> purchaseTicketAbstractChainContext;
    private final TicketAvailabilityTokenBucket ticketAvailabilityTokenBucket;

    @Override
    public List<TicketPageQueryVO> pageTicketQuery(TicketPageQueryParam ticketPageQueryParam) {
        // TODO 不考虑中转的情况，只考虑单程直达
        // 责任链模式：1.各个参数不能为空 2.出发日期不能小于当前日期 3.添加所有地区与车站的缓存，并校验这些名称是否真实存在
        ticketPageQueryAbstractChainContext.handler(TicketChainMarkEnum.TRAIN_QUERY_FILTER.name(), ticketPageQueryParam);

        // 将车站 code 转换为车站 id
        if (!redisTemplate.opsForHash().hasKey(STATION_CODE_ID_MAPPING, ticketPageQueryParam.getFromStationCode()) ||
                !redisTemplate.opsForHash().hasKey(STATION_CODE_ID_MAPPING, ticketPageQueryParam.getToStationCode())) {
            Station fromStation = this.getStationByCode(ticketPageQueryParam.getFromStationCode());
            Station toStation = this.getStationByCode(ticketPageQueryParam.getToStationCode());

            Map<String, Long> stationCodeAndIdMapping = new HashMap<>();
            stationCodeAndIdMapping.put(fromStation.getCode(), fromStation.getId());
            stationCodeAndIdMapping.put(toStation.getCode(), toStation.getId());
            redisTemplate.opsForHash().putAll(STATION_CODE_ID_MAPPING, stationCodeAndIdMapping);
        }
        long stationFromId = Long.parseLong(String.valueOf(redisTemplate.opsForHash().get(STATION_CODE_ID_MAPPING, ticketPageQueryParam.getFromStationCode())));
        long stationToId = Long.parseLong(String.valueOf(redisTemplate.opsForHash().get(STATION_CODE_ID_MAPPING, ticketPageQueryParam.getToStationCode())));

        // 使用 redis 对出发站和目标站的列车 id 取交集，获取可以直达的列车 id
        Set<Object> stationIdSet = redisTemplate.opsForSet().intersect(STATION_TRAIN_PASS_SET + stationFromId, STATION_TRAIN_PASS_SET + stationToId);
        if (CollUtil.isEmpty(stationIdSet)) {
            throw new ServiceException("出发地和目的地不存在直达");
        }
        // 校验这些列车是否真的可以从出发站直达目标站
        List<Long> stationIdList = new ArrayList<>();
        for (Object stationId : stationIdSet) {
            // 将 code 转化为名称
            String fromStationName = this.getStationByCode(ticketPageQueryParam.getFromStationCode()).getName();
            String toStationName = this.getStationByCode(ticketPageQueryParam.getToStationCode()).getName();
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
            ticketPageQueryVO.setTrainTag(Integer.parseInt(train.getTrainTag()));
            ticketPageQueryVO.setTrainBrand(train.getTrainBrand());
            ticketPageQueryVO.setStartStation(train.getStartStation());
            ticketPageQueryVO.setEndStation(train.getEndStation());
            ticketPageQueryVO.setStartRegion(train.getStartRegion());
            ticketPageQueryVO.setEndRegion(train.getEndRegion());
            ticketPageQueryVO.setDepartureTime(train.getDepartureTime());
            ticketPageQueryVO.setArrivalTime(train.getArrivalTime());
            ticketPageQueryVO.setSpendTime(spendTime);
            ticketPageQueryVO.setSeatTypeCountDTOList(seatTypeCountDTOList);
            ticketPageQueryVOList.add(ticketPageQueryVO);
        }
        return ticketPageQueryVOList;
    }

    @Override
    public TicketOrderDetailVO purchaseTickets(PurchaseTicketDetailParam purchaseTicket) {
        // 责任链模式，验证 1：参数不为空 2：参数是否有效 3：乘客是否重复买票
        purchaseTicketAbstractChainContext.handler(TicketChainMarkEnum.TRAIN_PURCHASE_TICKET_FILTER.name(), purchaseTicket);
        boolean tokenResult = ticketAvailabilityTokenBucket.takeTokenFromBucket(purchaseTicket);
        if (!tokenResult) {
            throw new ServiceException("列车站点已无余票");
        }
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
     * @param code 车站 code
     * @return 车站
     */
    private Station getStationByCode(String code) {
        Station station = distributedCache.get(TRAIN_STATION_INFO + code, Station.class);
        if (station == null) {
            LambdaQueryWrapper<Station> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Station::getCode, code);
            station = this.stationService.getOne(lqw);
            if (station == null) {
                throw new ServiceException("该车站 code 不存在");
            }
            distributedCache.put(TRAIN_STATION_INFO + code, station);
        }
        return station;
    }
}




