package com.zihuv.orderservice.mq.event;

import lombok.Data;

@Data
public class RefundEvent {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式
     */
    private String payCode;

    /**
     * 需要退款的金额
     */
    private String refundAmount;
}