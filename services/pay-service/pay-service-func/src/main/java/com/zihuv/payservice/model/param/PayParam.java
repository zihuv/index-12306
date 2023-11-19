package com.zihuv.payservice.model.param;

import com.zihuv.payservice.model.dto.GoodsDetailDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class PayParam {

    private String orderNo;

    @NotBlank(message = "支付方式不能为空")
    private String payCode;

    @NotBlank(message = "支付金额不能为空")
    private String price;

    @NotBlank(message = "订单标题不能为空")
    private String subject;

    private List<GoodsDetailDTO> goodsDetail;
}