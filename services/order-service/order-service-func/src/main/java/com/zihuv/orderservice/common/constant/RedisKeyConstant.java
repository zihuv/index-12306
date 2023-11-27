package com.zihuv.orderservice.common.constant;

import com.zihuv.orderservice.common.enums.OrderStatusEnum;

/**
 * Redis Key 定义常量类
 */
public class RedisKeyConstant {

    /**
     * 订单状态，prefix + orderNo。value 为 #{@link OrderStatusEnum} 的 code
     */
    public static final String ORDER_STATUS = "index12306-order-service:order_status:";
}