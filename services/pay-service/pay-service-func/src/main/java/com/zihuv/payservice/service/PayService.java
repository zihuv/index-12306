package com.zihuv.payservice.service;

import com.zihuv.payservice.model.param.PayParam;

import java.util.Map;

public interface PayService {

    /**
     * 支付
     *
     * @return orderNo 订单号
     */
    String pay(PayParam payParam);

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     */
    void closeTrade(String orderNo);

    /**
     * 查询支付信息
     *
     * @param orderNo 订单号
     * @return
     */
    String payQuery(String orderNo);

    /**
     * 退款
     *
     * @param orderNo      订单号
     * @param refundAmount 退款金额
     * @return 退款请求号（与订单号一致）
     */
    String refund(String orderNo, String refundAmount);

    /**
     * 查询退款信息
     *
     * @param orderNo      订单号
     * @param outRequestNo
     * @return
     */
    String refundQuery(String orderNo, String outRequestNo);

    void billDownload();

    String notifyOrderResult(Map<String, String> params);
}