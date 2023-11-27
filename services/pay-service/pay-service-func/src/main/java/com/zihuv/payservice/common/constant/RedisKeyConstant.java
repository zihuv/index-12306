package com.zihuv.payservice.common.constant;

/**
 * Redis Key 定义常量类
 */
public class RedisKeyConstant {

    /**
     * 支付状态 (key = prefix + md5,value = )
     */
    public static final String PAY_STATUS = "index12306-pay-service:pay_status:";
}