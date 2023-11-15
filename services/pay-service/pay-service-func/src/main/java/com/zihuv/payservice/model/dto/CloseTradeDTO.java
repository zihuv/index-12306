package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CloseTradeDTO {

    /**
     * 商户订单号
     */
    @JsonProperty("out_trade_no")
    private String orderNo;

    /**
     * 商家操作员编号 id
     */
    private final String operatorId = "YX01";
}