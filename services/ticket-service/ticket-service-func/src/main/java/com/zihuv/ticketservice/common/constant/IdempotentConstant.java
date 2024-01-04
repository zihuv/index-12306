package com.zihuv.ticketservice.common.constant;

public class IdempotentConstant {

    /**
     * 买票幂等性
     */
    public static final String PURCHASE_TICKETS = "index12306-ticket:idempotent:purchase-tickets";

    /**
     * 退票幂等性
     */
    public static final String RETURN_TICKETS = "index12306-ticket:idempotent:return-tickets";

    /**
     * 延时订单消费者幂等性
     */
    public static final String DELAY_ClOSE_ORDER_CONSUMER_KEY = "index12306-order:idempotent:delay-close-order:";

}