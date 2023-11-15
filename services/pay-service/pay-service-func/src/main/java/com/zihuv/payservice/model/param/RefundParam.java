package com.zihuv.payservice.model.param;

import lombok.Data;

/**
 * 退款参数
 */
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