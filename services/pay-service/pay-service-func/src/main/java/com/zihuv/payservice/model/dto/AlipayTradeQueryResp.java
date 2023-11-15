package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlipayTradeQueryResp {

    @JsonProperty("code")
    private String code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("buyer_logon_id")
    private String buyerLogonId;

    @JsonProperty("trade_status")
    private String tradeStatus;
}