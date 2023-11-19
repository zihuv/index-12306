package com.zihuv.orderservice.model.param;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车票订单创建请求参数
 */
@Data
public class TicketOrderCreateParam {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 车次 ID
     */
    private Long trainId;

    /**
     * 列车车次
     */
    private String trainNumber;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;

    /**
     * 出发时间
     */
    private LocalDateTime departureTime;

    /**
     * 出发时间
     */
    private LocalDateTime arrivalTime;
}