package com.zihuv.payservice.service.impl;

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
import com.zihuv.cache.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.orderservice.common.constant.RedisKeyConstant;
import com.zihuv.orderservice.common.enums.OrderStatusEnum;
import com.zihuv.payservice.config.AliPayProperties;
import com.zihuv.payservice.model.param.PayParam;
import com.zihuv.payservice.service.PayService;
import com.zihuv.payservice.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayServiceImpl implements PayService {

    private final DistributedCache distributedCache;
    private final AliPayProperties aliPayProperties;
    private final AlipayClient alipayClient;

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";


    @Override
    public void createPayPage(PayParam payParam) {
        PayDTO payDTO = new PayDTO();
        payDTO.setOrderNo(payParam.getOrderNo());
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
                throw new ServiceException("alipay 支付失败！订单号：" + payParam.getOrderNo());
            }
            FileUtil.writeString(response.getBody(), "C:\\Users\\10413\\Desktop\\temp\\ali.html", StandardCharsets.UTF_8);

            // 创建支付页面成功，修改订单状态
            distributedCache.put(RedisKeyConstant.ORDER_STATUS + payParam.getOrderNo(), OrderStatusEnum.PAYING.getCode());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
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
            // 订单关闭成功，修改订单状态
            distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.CLOSE.getCode());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
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
            String tradeStatus = payQueryRespDTO.getAlipayTradeQueryResp().getTradeStatus();
            // 订单关闭成功，修改订单状态
            // TODO 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
            if ("TRADE_CLOSED".equals(tradeStatus)) {
                distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.PAYING.getCode());
            }
            return tradeStatus;
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
            // 退款成功，修改订单状态
            distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.REFUNDED.getCode());
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
            String refundStatus = refundQueryRespDTO.getAlipayTradeFastpayRefundQueryResp().getRefundStatus();
            // 查询到退款成功，修改订单状态
            if ("REFUND_SUCCESS".equals(refundStatus)) {
                distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.REFUNDED.getCode());
            }

            return refundStatus;
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
    public String asyncPayNotifyPayOrder(Map<String, String> params) {
        // TODO 可能会出现重复接受异步通知的情况，可以使用 redis + lua，状态机
        // TODO 校验订单金额是否和商户订单一致
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
        // 支付成功，修改订单状态
        String orderNo = params.get("out_trade_no");
        distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.SUCCESS.getCode());
        log.info("支付成功，异步验签成功！");
        // 返回 success，支付宝将不再重复发送该支付通知
        return SUCCESS;
    }


}