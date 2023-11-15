package com.zihuv.payservice.model.param;

import lombok.Data;

/**
 * 查询退款参数
 */
@Data
public class RefundQueryParam {

    /**
     * 支付方式
     */
    private String payCode;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 退款请求号
     */
    private String outRequestNo;
}