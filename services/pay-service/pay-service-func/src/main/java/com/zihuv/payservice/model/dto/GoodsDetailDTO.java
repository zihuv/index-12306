package com.zihuv.payservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoodsDetailDTO {

    @JsonProperty("goods_id")
    private String goodsId;

    @JsonProperty("goods_name")
    private String goodsName;

    /**
     * 数量
     */
    @JsonProperty("quantity")
    private String quantity;
    
    @JsonProperty("price")
    private String price;
}