package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.ticketservice.common.enums.VehicleTypeEnum;
import com.zihuv.ticketservice.mapper.SeatMapper;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.entity.Seat;
import com.zihuv.ticketservice.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.zihuv.ticketservice.common.constant.Index12306Constant.SEAT_IS_NOT_OCCUPIED;

@Service
public class SeatServiceImpl extends ServiceImpl<SeatMapper, Seat> implements SeatService {

    @Override
    public List<SeatTypeCountDTO> listSeatTypeCount(Long trainId, String startStation, String endStation, Integer trainType) {
        // 根据列车类型获取乘车座位类型
        List<Integer> seatTypes = VehicleTypeEnum.findSeatTypesByCode(trainType);

        List<SeatTypeCountDTO> seatTypeCountList = new ArrayList<>();
        for (Integer seatType : seatTypes) {
            LambdaQueryWrapper<Seat> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Seat::getTrainId, trainId)
                    .eq(Seat::getStartStation, startStation)
                    .eq(Seat::getEndStation, endStation)
                    .eq(Seat::getSeatStatus, SEAT_IS_NOT_OCCUPIED)
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

    @Override
    public List<Seat> listSeatByTrainIdAndSeatType(String trainId, Integer seatType) {
        LambdaQueryWrapper<Seat> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Seat::getTrainId, trainId);
        lqw.eq(Seat::getSeatType, seatType);
        lqw.eq(Seat::getSeatStatus, SEAT_IS_NOT_OCCUPIED);
        return this.list(lqw);
    }

    @Override
    public List<Seat> listSeatAllByTrainId(String trainId) {
        LambdaQueryWrapper<Seat> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Seat::getTrainId, trainId);
        lqw.eq(Seat::getSeatStatus, SEAT_IS_NOT_OCCUPIED);
        return this.list(lqw);
    }

    @Override
    public List<String> listSeatCarriageByTrainIdAndSeatType(String trainId, Integer seatType) {
        LambdaQueryWrapper<Seat> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Seat::getTrainId, trainId);
        lqw.eq(Seat::getSeatStatus, SEAT_IS_NOT_OCCUPIED);
        lqw.eq(Seat::getSeatType, seatType);
        lqw.select(Seat::getCarriageNumber);
        List<Seat> seatList = this.list(lqw);

        Set<String> seatCarriageList = new LinkedHashSet<>();
        for (Seat seat : seatList) {
            seatCarriageList.add(seat.getCarriageNumber());
        }
        return new ArrayList<>(seatCarriageList);
    }

}




