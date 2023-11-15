package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefundRespDTO {

    @JsonProperty("alipay_trade_refund_response")
    private AlipayTradeRefundResp alipayTradeRefundResp;

    private String sign;
}