package com.zihuv.payservice.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.zihuv.base.util.JSON;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.payservice.config.AliPayProperties;
import com.zihuv.payservice.model.dto.*;
import com.zihuv.payservice.model.param.PayParam;
import com.zihuv.payservice.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayServiceImpl implements PayService {

    private final AliPayProperties aliPayProperties;
    private final AlipayClient alipayClient;

    @Value("${index-12306.tail-number-strategy:userId}")
    private String tailNumberStrategy;

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    private String getOrderNo() {
        // 日期。如：231114
        String date = LocalDateTimeUtil.format(LocalDateTime.now(), "yyMMdd");
        // 时间戳。一天 86400000 毫秒，计算后缩短订单号长度
        String timestamp = String.valueOf(System.currentTimeMillis() % 86400000);
        // 尾号
        String tailNumber = "000000";
        if ("userId".equals(tailNumberStrategy)) {
            // 用户 id
            tailNumber = String.valueOf(UserContext.getUserId() % 10000);
        } else if ("random".equals(tailNumberStrategy)) {
            // 随机数
            int min = 100000;
            int max = 999999;
            tailNumber = String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
        return date + timestamp + tailNumber;
    }

    @Override
    public String pay(PayParam payParam) {
        String orderNo = this.getOrderNo();

        PayDTO payDTO = new PayDTO();
        payDTO.setOrderNo(orderNo);
        payDTO.setPrice(payParam.getPrice());
        payDTO.setSubject(payParam.getSubject());
        payDTO.setGoodsDetail(payParam.getGoodsDetail());

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // 异步接收地址，仅支持http/https，公网可访问
        request.setNotifyUrl(aliPayProperties.getNotifyUrl());
        // 同步跳转地址，仅支持http/https
        request.setReturnUrl(aliPayProperties.getReturnUrl());
        request.setBizContent(JSON.toJsonStr(payDTO));

        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (!response.isSuccess()) {
                throw new ServiceException("alipay 支付失败！订单号：" + orderNo);
            }
            FileUtil.writeString(response.getBody(), "C:\\Users\\10413\\Desktop\\temp\\ali.html", StandardCharsets.UTF_8);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        return orderNo;
    }

    @Override
    public void closeTrade(String orderNo) {
        // TODO 一定时间内未支付调用该方法
        // 支付宝创建订单时机：1.扫码但未支付 2.支付页面登录但未支付
        // 若支付宝未创建订单，仅需要关闭本地订单即可
        // 调用用支付宝的交易关闭接口
        CloseTradeDTO closeTradeDTO = new CloseTradeDTO();
        closeTradeDTO.setOrderNo(orderNo);

        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent(JSON.toJsonStr(closeTradeDTO));
        try {
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                throw new ServiceException(StrUtil.format("订单：{} 关闭失败", orderNo));
            }
            log.info("订单：{} 关闭成功", orderNo);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }


        // TODO 更新用户订单状态

    }

    @Override
    public String payQuery(String orderNo) {
        PayDTO payDTO = new PayDTO();
        payDTO.setOrderNo(orderNo);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(JSON.toJsonStr(payDTO));

        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                throw new ServiceException(StrUtil.format("订单：{} 查询失败！可能订单并未创建", orderNo));
            }
            PayQueryRespDTO payQueryRespDTO = JSON.toBean(response.getBody(), PayQueryRespDTO.class);
            return payQueryRespDTO.getAlipayTradeQueryResp().getTradeStatus();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String refund(String orderNo, String refundAmount) {
        RefundDTO refundDTO = new RefundDTO();
        refundDTO.setOrderNo(orderNo);
        refundDTO.setRefundAmount(refundAmount);
        refundDTO.setOutRequestNo(orderNo);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(JSON.toJsonStr(refundDTO));

        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                throw new ServiceException(StrUtil.format("订单：{} 退款失败", orderNo));
            }
            // 状态码 10000 代表响应成功，并不代表已经完成退款
            // 当参数为 fund_change = Y 时，代表已经完成退款
            // 当 fund_change = N 时，需进一步调用退款查询接口判断
            RefundRespDTO refundRespDTO = JSON.toBean(response.getBody(), RefundRespDTO.class);
            if (!"Y".equals(refundRespDTO.getAlipayTradeRefundResp().getFundChange())) {
                throw new ServiceException(StrUtil.format("订单：{} 退款状态未知，请调用退款查询接口", orderNo));
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        return orderNo;
    }

    @Override
    public String refundQuery(String orderNo, String outRequestNo) {
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderNo);
        bizContent.put("out_request_no", outRequestNo);

        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        request.setBizContent(bizContent.toString());

        try {
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                throw new ServiceException(StrUtil.format("订单：{} 退款查询失败", orderNo));
            }
            RefundQueryRespDTO refundQueryRespDTO = JSON.toBean(response.getBody(), RefundQueryRespDTO.class);
            return refundQueryRespDTO.getAlipayTradeFastpayRefundQueryResp().getRefundStatus();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void billDownload() {
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
        model.setBillType("trade");
        // 只能下载前一日24点前的账单数据
        model.setBillDate("2023-11-11");
        request.setBizModel(model);
        AlipayDataDataserviceBillDownloadurlQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.getBody());
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // 获取诊断链接
            String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            System.out.println(diagnosisUrl);
        }
    }

    @Override
    public String notifyOrderResult(Map<String, String> params) {
        // TODO 可能会出现重复接受异步通知的情况，可以使用 redis + lua，状态机
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    aliPayProperties.getAlipayPublicKey(),
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        if (!signVerified) {
            // 验签失败，打印日志
            log.error("支付成功，但异步验签失败！");
            return FAIL;
        }

        // 当交易通知状态为 TRADE_SUCCESS 时，买家才算付款成功
        String tradeStatus = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(tradeStatus)) {
            log.error("支付未成功");
            return FAIL;
        }

        log.info("支付成功，异步验签成功！");
        // 返回 success，支付宝将不再重复发送该支付通知
        return SUCCESS;
    }


}