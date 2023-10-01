package com.zihuv.ticketservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.entity.Seat;

import java.util.List;

public interface SeatService extends IService<Seat> {

    List<SeatTypeCountDTO> listSeatTypeCount(Long trainId, String startStation, String endStation, List<Integer> seatTypes);
}
