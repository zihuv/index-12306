package com.zihuv.payservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    /**
     * 未支付
     */
    NOT_PAY("未支付"),

    /**
     * 支付成功
     */
    SUCCESS("支付成功"),

    /**
     * 超时已关闭
     */
    CLOSE("超时已关闭"),

    /**
     * 用户已取消
     */
    CANCEL("用户已取消"),

    /**
     * 退款中
     */
    REFUNDING("退款中"),

    /**
     * 已退款
     */
    REFUNDED("已退款"),

    /**
     * 退款异常
     */
    REFUND_ERROR("退款异常");

    /**
     * 订单状态
     */
    private final String type;
}
