package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlipayTradeRefundResp {

    @JsonProperty("out_trade_no")
    private String orderNo;

    @JsonProperty("buyer_logon_id")
    private String buyerLogonId;

    @JsonProperty("refund_fee")
    private String refundFee;

    /**
     * 本次退款是否发生了资金变化，如：Y,N
     */
    @JsonProperty("fund_change")
    private String fundChange;
}