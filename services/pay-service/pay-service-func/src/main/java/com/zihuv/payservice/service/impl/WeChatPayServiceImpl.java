package com.zihuv.payservice.service.impl;

import com.zihuv.payservice.model.param.PayParam;
import com.zihuv.payservice.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeChatPayServiceImpl implements PayService {

    @Override
    public String pay(PayParam payParam) {
        System.out.println("微信——支付");
        return "微信——支付";
    }

    @Override
    public void closeTrade(String orderNo) {
        System.out.println("微信——取消订单");
    }

    @Override
    public String payQuery(String orderNo) {
        System.out.println("微信——查询订单");
        return "微信——查询订单";
    }

    @Override
    public String refund(String orderNo, String refundAmount) {
        System.out.println("微信——退款");
        return "微信——退款";
    }

    @Override
    public String refundQuery(String orderNo, String outRequestNo) {
        System.out.println("微信——查询退款");
        return "微信——查询退款";
    }

    @Override
    public void billDownload() {
        System.out.println("微信——下载账单");
    }

    @Override
    public String notifyOrderResult(Map<String, String> params) {
        return null;
    }
}