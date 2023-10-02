package com.zihuv.ticketservice.service;

import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.model.entity.TrainStation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.ticketservice.model.vo.TrainStationVO;

import java.util.List;

public interface TrainStationService extends IService<TrainStation> {

    /**
     * 根据列车 id 查询所有车站
     *
     * @param trainId 列车id
     * @return 该列车经过的站点集合
     */
    List<TrainStationVO> listTrainStationVO(String trainId);

    /**
     * 根据列车 id ，查询该列车的总路线
     *
     * @param trainId   列车 ID
     * @return 列车站点路线关系信息
     */
    List<RouteDTO> listTrainStationRoute(String trainId);

    /**
     * 根据列车 id 和出发到达站，查询该列车这段路程的路线
     *
     * @param trainId   列车 ID
     * @param departure 出发站
     * @param arrival   到达站
     * @return 列车站点路线关系信息
     */
    List<RouteDTO> listTrainStationRoute(String trainId, String departure, String arrival);
}
