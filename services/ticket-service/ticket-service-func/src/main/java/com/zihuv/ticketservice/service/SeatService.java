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

    /**
     * 根据列车 id 和座位类型座位，查询座位
     *
     * @param trainId  列车 id
     * @param seatType 座位类型
     * @return 座位集合
     */
    List<Seat> listSeatByTrainIdAndSeatType(String trainId, Integer seatType);

    /**
     * 根据列车 id ，查询所有座位
     *
     * @param trainId 列车 id
     * @return 座位集合
     */
    List<Seat> listSeatAllByTrainId(String trainId);

    /**
     * 根据列车 id 和座位类型，查询所有车厢
     *
     * @param trainId  列车 id
     * @param seatType 座位类型
     * @return 列车车厢
     */
    List<String> listSeatCarriageByTrainIdAndSeatType(String trainId, Integer seatType);
}
