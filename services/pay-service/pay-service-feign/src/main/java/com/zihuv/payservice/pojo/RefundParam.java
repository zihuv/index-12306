package com.zihuv.payservice.pojo;

import lombok.Data;

@Data
public class RefundParam {

    /**
     * 支付方式
     */
    private String payCode;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 需要退款的金额
     */
    private String refundAmount;
}