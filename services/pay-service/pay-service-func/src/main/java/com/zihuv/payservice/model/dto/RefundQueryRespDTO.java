package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefundQueryRespDTO {

    @JsonProperty("alipay_trade_fastpay_refund_query_response")
    private AlipayTradeFastpayRefundQueryResp alipayTradeFastpayRefundQueryResp;

    private String sign;
}