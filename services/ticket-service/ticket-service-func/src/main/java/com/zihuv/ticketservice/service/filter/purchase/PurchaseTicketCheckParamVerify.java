package com.zihuv.ticketservice.service.filter.purchase;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zihuv.DistributedCache;
import com.zihuv.base.util.JSON;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.ticketservice.common.constant.Index12306Constant;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.model.entity.TrainStation;
import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import com.zihuv.ticketservice.service.TrainService;
import com.zihuv.ticketservice.service.TrainStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.TRAIN_INFO;
import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.TRAIN_STATION_STOPOVER_DETAIL;

@Component
@RequiredArgsConstructor
public class PurchaseTicketCheckParamVerify implements PurchaseTicketChainFilter<PurchaseTicketDetailParam> {

    @Value("${spring.profiles.active}")
    private String environment;

    private final DistributedCache distributedCache;
    private final TrainService trainService;
    private final TrainStationService trainStationService;

    @Override
    public void handler(PurchaseTicketDetailParam requestParam) {
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
        if (!"dev".equals(environment)) {
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
            throw new ClientException("列车车站数据错误");
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