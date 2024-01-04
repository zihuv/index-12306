package com.zihuv.payservice.common.constant;

public class IdempotentConstant {

    public static final String REFUND = "index12306-pay-service:idempotent:refund";

    public static final String ASYNC_PAY_NOTIFY = "index12306-pay-service:idempotent:async-pay-notify";

    public static final String REFUND_CONSUMER_KEY = "index12306-pay-service:idempotent:refund-consumer";
}