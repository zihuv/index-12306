package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlipayTradeFastpayRefundQueryResp {

    private String code;

    private String msg;

    /**
     * 业务返回码描述
     */
    @JsonProperty("sub_msg")
    private String subMsg;

    @JsonProperty("refund_status")
    private String refundStatus;
}