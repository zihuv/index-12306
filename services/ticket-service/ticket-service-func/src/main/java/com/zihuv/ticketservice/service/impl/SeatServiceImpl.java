package com.zihuv.ticketservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.entity.Seat;
import com.zihuv.ticketservice.service.SeatService;
import com.zihuv.ticketservice.mapper.SeatMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    @Override
    public List<SeatTypeCountDTO> listSeatTypeCount(Long trainId, String startStation, String endStation, List<Integer> seatTypes) {
        List<SeatTypeCountDTO> seatTypeCountList = new ArrayList<>();
        for (Integer seatType : seatTypes) {
            LambdaQueryWrapper<Seat> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Seat::getTrainId, trainId)
                    .eq(Seat::getStartStation, startStation)
                    .eq(Seat::getEndStation, endStation)
                    .eq(Seat::getSeatStatus, "0")
                    .eq(Seat::getSeatType, seatType)
                    .groupBy(Seat::getSeatType)
                    .select(Seat::getSeatType);

            SeatTypeCountDTO seatTypeCountDTO = new SeatTypeCountDTO();
            seatTypeCountDTO.setSeatType(seatType);
            seatTypeCountDTO.setSeatCount(Math.toIntExact(this.count(lqw)));
            seatTypeCountList.add(seatTypeCountDTO);
        }
        return seatTypeCountList;
    }
}




