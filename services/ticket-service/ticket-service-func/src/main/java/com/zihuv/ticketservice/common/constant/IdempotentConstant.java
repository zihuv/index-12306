package com.zihuv.ticketservice.common.constant;

public class IdempotentConstant {

    /**
     * 买票幂等性
     */
    public static final String PURCHASE_TICKETS = "index12306-ticket:idempotent:purchase-tickets";

    /**
     * 延时订单幂等性
     */
    public static final String DELAY_CLOSE_ORDER = "index12306-order:idempotent:delay-close-order:";
}