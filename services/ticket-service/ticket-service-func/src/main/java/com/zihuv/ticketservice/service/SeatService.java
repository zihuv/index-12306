package com.zihuv.ticketservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.entity.Seat;

import java.util.List;

public interface SeatService extends IService<Seat> {

    /***
     * 获取座位类型以及对应的数量的集合
     *
     * @param trainId 列车 id
     * @param startStation 起始站
     * @param endStation 到达站
     * @param trainType 列车类型
     * @return java.util.List<com.zihuv.ticketservice.model.dto.SeatTypeCountDTO>
     */
    List<SeatTypeCountDTO> listSeatTypeCount(Long trainId, String startStation, String endStation, Integer trainType);
}
