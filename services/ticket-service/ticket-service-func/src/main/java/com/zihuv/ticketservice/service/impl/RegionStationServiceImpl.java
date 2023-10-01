package com.zihuv.ticketservice.service.impl;

import com.zihuv.DistributedCache;
import com.zihuv.base.util.JSON;
import com.zihuv.ticketservice.model.entity.Station;
import com.zihuv.ticketservice.model.vo.StationVO;
import com.zihuv.ticketservice.service.RegionStationService;
import com.zihuv.ticketservice.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.STATION_INFO_ALL;

@Service
@RequiredArgsConstructor
public class RegionStationServiceImpl implements RegionStationService {

    private final DistributedCache distributedCache;
    private final StationService stationService;

    @Override
    public List<StationVO> listAllStation() {
        String jsonStrList = distributedCache.safeGet(STATION_INFO_ALL, String.class, () -> {
            List<Station> stationList = stationService.list();
            return JSON.toJsonStr(stationList);
        });
        return JSON.toList(jsonStrList, StationVO.class);
    }

}