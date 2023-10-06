package com.zihuv.ticketservice.service.filter.purchase;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zihuv.DistributedCache;
import com.zihuv.base.util.JSON;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.ticketservice.common.constant.Index12306Constant;
import com.zihuv.ticketservice.common.enums.VehicleTypeEnum;
import com.zihuv.ticketservice.model.dto.TicketPurchasePassengerDTO;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.model.entity.TrainStation;
import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;
import com.zihuv.ticketservice.service.TrainService;
import com.zihuv.ticketservice.service.TrainStationService;
import com.zihuv.userservice.feign.UserPassengerFeign;
import com.zihuv.userservice.pojo.PassengerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.TRAIN_INFO;
import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.TRAIN_STATION_STOPOVER_DETAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class PurchaseTicketCheckParamVerifyHandler implements PurchaseTicketChainFilter<TicketPurchaseDetailParam> {

    @Value("${index-12306.check-purchase-ticket-time:true}")
    private boolean checkPurchaseTicketTime;

    private final DistributedCache distributedCache;
    private final TrainService trainService;
    private final TrainStationService trainStationService;
    private final UserPassengerFeign userPassengerFeign;

    @Override
    public void handler(TicketPurchaseDetailParam requestParam) {
        // 查询该车次是否存在
        Train train = distributedCache.safeGet(
                TRAIN_INFO + requestParam.getTrainId(),
                Train.class,
                () -> trainService.getById(requestParam.getTrainId()),
                Index12306Constant.ADVANCE_TICKET_DAY,
                TimeUnit.DAYS);
        if (Objects.isNull(train)) {
            throw new ClientException("请检查车次是否存在");
        }
        // 开发环境，无视车票时间
        if (checkPurchaseTicketTime) {
            // 判断当前买票的时间是否有效
            if (LocalDateTime.now().isAfter(train.getSaleTime())) {
                throw new ClientException("列车车次暂未发售");
            }
            if (LocalDateTime.now().isBefore(train.getDepartureTime())) {
                throw new ClientException("列车车次已出发，禁止购票");
            }
        }

        // 车站是否存在车次中，车站的顺序是否正确
        String trainStationStopoverDetailJson = distributedCache.safeGet(
                TRAIN_STATION_STOPOVER_DETAIL + requestParam.getTrainId(),
                String.class,
                () -> {
                    LambdaQueryWrapper<TrainStation> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(TrainStation::getTrainId, requestParam.getTrainId());
                    lqw.select(TrainStation::getDeparture);
                    List<TrainStation> actualTrainStationList = trainStationService.list(lqw);
                    return CollUtil.isNotEmpty(actualTrainStationList) ? JSON.toJsonStr(actualTrainStationList) : null;
                },
                Index12306Constant.ADVANCE_TICKET_DAY,
                TimeUnit.DAYS
        );
        List<TrainStation> trainStationList = JSON.toList(trainStationStopoverDetailJson, TrainStation.class);
        boolean validateStation = validateStation(
                trainStationList.stream().map(TrainStation::getDeparture).toList(),
                requestParam.getDeparture(),
                requestParam.getArrival()
        );
        if (!validateStation) {
            throw new ClientException(StrUtil.format("该列车并没有从[{}]至[{}]的路线", requestParam.getDeparture(), requestParam.getArrival()));
        }
        // 校验这些乘车人是否被该用户添加
        List<PassengerVO> passengerList = userPassengerFeign.listPassengerVO(String.valueOf(UserContext.getUserId())).getData();
        if (CollUtil.isEmpty(passengerList)) {
            throw new ClientException("该用户并没有添加乘车人");
        }
        for (PassengerVO passenger : passengerList) {
            for (TicketPurchasePassengerDTO requestParamPassenger : requestParam.getPassengers()) {
                if (!requestParamPassenger.getPassengerId().equals(passenger.getPassengerId())) {
                    throw new ClientException("该乘车人并未被添加到该用户当中");
                }
            }
        }
        // 校验乘坐人所选的座位类型是否在该列车中存在
        List<Integer> seatTypesByCode = VehicleTypeEnum.findSeatTypesByCode(train.getTrainType());
        for (TicketPurchasePassengerDTO passenger : requestParam.getPassengers()) {
            boolean hasSeatTypeInTrain = false;
            for (Integer seatType : seatTypesByCode) {
                if (passenger.getSeatType().equals(seatType)) {
                    hasSeatTypeInTrain = true;
                    break;
                }
            }
            if (!hasSeatTypeInTrain) {
                throw new ClientException("该列车并没有您所选的座位类型");
            }
        }

    }

    @Override
    public int getOrder() {
        return 2;
    }

    public boolean validateStation(List<String> stationList, String startStation, String endStation) {
        int index1 = stationList.indexOf(startStation);
        int index2 = stationList.indexOf(endStation);
        if (index1 == -1 || index2 == -1) {
            return false;
        }
        return index2 >= index1;
    }
}