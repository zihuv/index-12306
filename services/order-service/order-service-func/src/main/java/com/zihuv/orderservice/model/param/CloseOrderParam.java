package com.zihuv.orderservice.model.param;

import lombok.Data;

@Data
public class CloseOrderParam {

    private String orderNo;

    private Integer closeCode;
}