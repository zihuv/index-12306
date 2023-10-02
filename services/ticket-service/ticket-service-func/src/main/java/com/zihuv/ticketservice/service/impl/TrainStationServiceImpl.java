package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.DistributedCache;
import com.zihuv.base.util.JSON;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.model.entity.TrainStation;
import com.zihuv.ticketservice.model.vo.TrainStationVO;
import com.zihuv.ticketservice.service.TrainStationService;
import com.zihuv.ticketservice.mapper.TrainStationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.TRAIN_PASS_STATION_INFO;

@Service
@RequiredArgsConstructor
public class TrainStationServiceImpl extends ServiceImpl<TrainStationMapper, TrainStation> implements TrainStationService {

    private final DistributedCache distributedCache;

    @Override
    public List<TrainStationVO> listTrainStationVO(String trainId) {
        String trainStationJson = this.getTrainStationJsonByTrainId(trainId);
        return JSON.toList(trainStationJson, TrainStationVO.class);
    }

    @Override
    public List<RouteDTO> listTrainStationRoute(String trainId) {
        String trainStationJson = this.getTrainStationJsonByTrainId(trainId);
        return JSON.toList(trainStationJson, RouteDTO.class);
    }

    @Override
    public List<RouteDTO> listTrainStationRoute(String trainId, String departure, String arrival) {
        List<RouteDTO> routeDTOList = this.listTrainStationRoute(trainId);
        // 根据 departure arrival 截取集合
        boolean flag = false;
        List<RouteDTO> passengerRouteList = new ArrayList<>();
        for (RouteDTO routeDTO : routeDTOList) {
            if (departure.equals(routeDTO.getStartStation())) {
                flag = true;
            }
            if (flag) {
                passengerRouteList.add(routeDTO);
            }
            if (arrival.equals(routeDTO.getEndStation())) {
                break;
            }
        }
        if (passengerRouteList.get(passengerRouteList.size() - 1).getEndStation() == null) {
            return new ArrayList<>();
        }
        return passengerRouteList;
    }

    /**
     * 根据列车 id 查询该列车的所有会经过的站点
     *
     * @param trainId 列车 id
     * @return java.lang.String 列车经过站点 json
     */
    private String getTrainStationJsonByTrainId(String trainId) {
        return distributedCache.safeGet(TRAIN_PASS_STATION_INFO + trainId, String.class, () -> {
            LambdaQueryWrapper<TrainStation> lqw = new LambdaQueryWrapper<>();
            lqw.eq(TrainStation::getTrainId, trainId);
            List<TrainStation> trainStationList = this.list(lqw);
            return JSON.toJsonStr(trainStationList);
        });
    }
}




