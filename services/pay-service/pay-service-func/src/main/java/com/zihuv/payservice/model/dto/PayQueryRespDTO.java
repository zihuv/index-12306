package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayQueryRespDTO {

    @JsonProperty("alipay_trade_query_response")
    private AlipayTradeQueryResp alipayTradeQueryResp;

    @JsonProperty("sign")
    private String sign;
}