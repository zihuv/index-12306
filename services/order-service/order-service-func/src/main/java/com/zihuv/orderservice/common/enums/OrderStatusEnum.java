package com.zihuv.orderservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    /**
     * 未支付
     */
    NOT_PAY(0, "未支付"),

    /**
     * 支付中
     */
    PAYING(1, "支付中"),

    /**
     * 支付成功
     */
    SUCCESS(2, "支付成功"),

    /**
     * 支付失败
     */
    FAIL(3, "支付失败"),

    /**
     * 支付取消
     */
    CANCEL(4, "支付取消"),

    /**
     * 退款成功
     */
    REFUNDED(5, "退款成功"),

    /**
     * 订单关闭
     */
    CLOSE(6, "订单关闭");


    /**
     * 订单状态码
     */
    private final Integer code;

    /**
     * 订单状态
     */
    private final String type;
}
