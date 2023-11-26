package com.zihuv.ticketservice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.service.TrainStationService;
import com.zihuv.ticketservice.model.entity.Seat;
import com.zihuv.ticketservice.service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class InitialSeatDataTest {

    @Autowired
    private SeatService seatService;
    @Autowired
    private TrainStationService trainStationService;

    @Test
    public void test01() {
        List<RouteDTO> routeDTOS = trainStationService.listTrainStationRoute("1");

        LambdaQueryWrapper<Seat> lqw = new LambdaQueryWrapper<>();
        for (RouteDTO routeDTO : routeDTOS) {
            for (int carriageNumber = 1; carriageNumber < 3; carriageNumber++) {
                for (int seatNumberCode = 1; seatNumberCode < 7; seatNumberCode++) {
                    for (int seatNumberLetter = 0; seatNumberLetter < 6; seatNumberLetter++) {
                        Seat seat = new Seat();
                        seat.setTrainId(1L);
                        // 01 02
                        seat.setCarriageNumber("0" + carriageNumber);
                        // 01A B C D F - 06
                        seat.setSeatNumber("0" + seatNumberCode + (char) ('A' + seatNumberLetter));
                        seat.setSeatLetter(String.valueOf((char) ('A' + seatNumberLetter)));
                        seat.setSeatType(2);
                        seat.setStartStation(routeDTO.getStartStation());
                        seat.setEndStation(routeDTO.getEndStation());
                        seat.setSeatStatus(0);
                        seat.setPrice(50);
                        seatService.save(seat);
                    }
                }
            }
        }


    }
}