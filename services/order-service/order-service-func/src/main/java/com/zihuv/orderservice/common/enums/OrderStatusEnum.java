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
     * 支付成功
     */
    SUCCESS(1, "支付成功"),

    /**
     * 超时已关闭
     */
    CLOSE(2, "超时已关闭"),

    /**
     * 用户已取消
     */
    CANCEL(3, "用户已取消"),

    /**
     * 退款中
     */
    REFUNDING(4, "退款中"),

    /**
     * 已退款
     */
    REFUNDED(5, "已退款"),

    /**
     * 退款异常
     */
    REFUND_ERROR(6, "退款异常");


    /**
     * 订单状态码
     */
    private final Integer code;

    /**
     * 订单状态
     */
    private final String type;
}
