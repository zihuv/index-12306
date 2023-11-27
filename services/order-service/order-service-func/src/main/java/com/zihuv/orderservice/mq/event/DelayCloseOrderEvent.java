package com.zihuv.orderservice.mq.event;

import com.zihuv.orderservice.model.dto.PassengerInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * 乘车人购票信息
     */
    private PassengerInfoDTO passengerInfoDTO;
}