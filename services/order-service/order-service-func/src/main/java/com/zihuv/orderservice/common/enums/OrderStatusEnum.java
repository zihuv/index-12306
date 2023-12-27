package com.zihuv.orderservice.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    //----- 初始状态 -----
    /**
     * 未支付
     */
    NOT_PAY(10, "未支付"),


    //----- 中间状态 -----
    /**
     * 支付中
     */
    PAYING(20, "支付中"),


    //----- 完成状态 -----
    /**
     * 支付成功
     */
    SUCCESS(30, "支付成功"),


    //----- 订单生命周期结束 -----
    /**
     * 用户取消订单（必须先校验[支付成功]）
     */
    CANCEL(40, "用户取消订单"),

    /**
     * 退款成功（必须先校验[支付成功]）
     */
    REFUNDED(41, "退款成功"),

    /**
     * 订单超时关闭（必须先校验[支付成功]）
     */
    TIMEOUT(42, "订单超时关闭"),

    /**
     * 订单正常关闭（若已经支付成功，但此时的状态不再允许退款）
     */
    CLOSE(43, "订单正常关闭");


    /**
     * 订单状态码
     */
    private final Integer code;

    /**
     * 订单状态
     */
    private final String type;
}
