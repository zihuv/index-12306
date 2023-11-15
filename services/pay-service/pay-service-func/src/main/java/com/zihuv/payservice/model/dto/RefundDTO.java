package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefundDTO {

    /**
     * 商户订单号
     */
    @JsonProperty("out_trade_no")
    private String orderNo;

    /**
     * 退款金额
     */
    @JsonProperty("refund_amount")
    private String refundAmount;

    /**
     * 退款请求号
     */
    @JsonProperty("out_request_no")
    private String outRequestNo;
}