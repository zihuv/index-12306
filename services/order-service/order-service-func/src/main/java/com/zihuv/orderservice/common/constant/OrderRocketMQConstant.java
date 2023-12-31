package com.zihuv.orderservice.common.constant;

public class OrderRocketMQConstant {

    /**
     * 订单服务相关业务 Topic Key
     */
    public static final String ORDER_DELAY_CLOSE_TOPIC_KEY = "index12306_order-service_delay-close-order_topic";

    /**
     * 购票服务创建订单后延时关闭业务 Tag Key
     */
    public static final String ORDER_DELAY_CLOSE_TAG_KEY = "index12306_order-service_delay-close-order_tag";


    public static final String ORDER_PRODUCER_GROUP = "ORDER_PRODUCER_GROUP";

    /**
     * 订单服务相关业务 Topic Key
     */
    public static final String ORDER_REFUND_TOPIC = "index12306_order-service_refund_topic";

    /**
     * 购票服务创建订单后延时关闭业务 Tag Key
     */
    public static final String ORDER_REFUND_TAG = "index12306_order-service_refund_tag";

}
