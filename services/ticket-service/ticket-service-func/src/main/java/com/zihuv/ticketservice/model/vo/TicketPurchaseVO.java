package com.zihuv.ticketservice.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class TicketPurchaseVO {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 乘车人订单详情
     */
    private List<TicketOrderDetailVO> ticketOrderDetails;
}