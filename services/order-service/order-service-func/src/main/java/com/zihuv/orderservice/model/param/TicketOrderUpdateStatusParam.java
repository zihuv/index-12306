package com.zihuv.orderservice.model.param;

import lombok.Data;

@Data
public class TicketOrderUpdateStatusParam {

    private String orderNo;

    private Integer status;
}