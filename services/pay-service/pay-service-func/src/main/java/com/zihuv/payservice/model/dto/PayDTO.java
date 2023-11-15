package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PayDTO {

    /**
     * 商户订单号
     */
    @JsonProperty("out_trade_no")
    private String orderNo;

    /**
     * 支付金额
     */
    @JsonProperty("total_amount")
    private String price;

    /**
     * 订单标题
     */
    @JsonProperty("subject")
    private String subject;

    /**
     * 电脑网站支付场景固定传值 FAST_INSTANT_TRADE_PAY
     */
    @JsonProperty("product_code")
    private final String productCode = "FAST_INSTANT_TRADE_PAY";

    @JsonProperty("goods_detail")
    private List<GoodsDetailDTO> goodsDetail;

}