package com.zihuv.orderservice.mq.event;

import com.zihuv.orderservice.model.dto.PassengerInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayCloseOrderEvent {

    /**
     * 车次 ID
     */
    private String trainId;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 列车车次
     */
    private String trainNumber;

    /**
     * 订单金额
     */
    private String money;

    /**
     * 出发时间
     */
    private LocalDateTime departureTime;

    /**
     * 出发时间
     */
    private LocalDateTime arrivalTime;

    /**
     * 乘车人购票信息
     */
    private PassengerInfoDTO passengerInfoDTO;
}