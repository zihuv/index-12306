package com.zihuv.payservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.payservice.common.enums.PayStrategyEnum;
import com.zihuv.payservice.model.param.PayParam;
import com.zihuv.payservice.model.param.RefundParam;
import com.zihuv.payservice.model.param.RefundQueryParam;
import com.zihuv.payservice.service.PayService;
import com.zihuv.payservice.service.strategy.pay.PayStrategyContext;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "支付管理")
@Validated
@RestController
@RequiredArgsConstructor
public class PayController {

    private final PayStrategyContext payStrategyContext;

    /**
     * 支付
     *
     * @param payParam 支付参数
     * @return com.zihuv.convention.result.Result<?>
     */
    @Operation(summary = "创建支付页面")
    @PostMapping("/api/pay-service/pay")
    public Result<?> createPayPage(@Valid @RequestBody PayParam payParam) {
        PayService payService = payStrategyContext.getPayService(payParam.getPayCode());
        payService.createPayPage(payParam);
        return Result.success();
    }

    /**
     * 取消交易
     *
     * @param payCode 支付方式
     * @param orderNo 订单号
     * @return com.zihuv.convention.result.Result<?>
     */
    @Operation(summary = "取消交易")
    @GetMapping("/api/pay-service/close")
    public Result<?> closeTrade(@RequestParam String payCode, @RequestParam String orderNo) {
        PayService payService = payStrategyContext.getPayService(payCode);
        payService.closeTrade(orderNo);
        return Result.success();
    }

    /**
     * 查询交易信息
     *
     * @param payCode 支付方式
     * @param orderNo 订单号
     * @return com.zihuv.convention.result.Result<?>
     */
    @Operation(summary = "查询交易信息")
    @GetMapping("/api/pay-service/query")
    public Result<?> payQuery(@RequestParam String payCode, @RequestParam String orderNo) {
        // TODO 设置定时任务，在支付宝没有通知的情况下，主动去查询支付宝中的订单
        // 支付宝没有主动通知的情况：1.支付宝订单未创建 2.订单未支付 3.订单已支付但没通知成功
        PayService payService = payStrategyContext.getPayService(payCode);
        String tradeStatus = payService.payQuery(orderNo);
        return Result.success(tradeStatus);
    }

    /**
     * 退款
     *
     * @param refundParam 退款参数
     * @return com.zihuv.convention.result.Result<?>
     */
    @Operation(summary = "退款")
    @PostMapping("/api/pay-service/refund")
    public Result<?> refund(@RequestBody RefundParam refundParam) {
        PayService payService = payStrategyContext.getPayService(refundParam.getPayCode());
        String outRequestNo = payService.refund(refundParam.getOrderNo(), refundParam.getRefundAmount());
        return Result.success(outRequestNo);
    }

    /**
     * 查询退款信息
     *
     * @param refundQueryParam 查询退款参数
     * @return com.zihuv.convention.result.Result<?>
     */
    @Operation(summary = "查询退款信息")
    @PostMapping("/api/pay-service/refund/query")
    public Result<?> refundQuery(@RequestBody RefundQueryParam refundQueryParam) {
        PayService payService = payStrategyContext.getPayService(refundQueryParam.getPayCode());
        String refundStatus = payService.refundQuery(refundQueryParam.getOrderNo(), refundQueryParam.getOutRequestNo());
        return Result.success(refundStatus);
    }

    /**
     * 下载账单
     *
     * @param payCode 支付方式
     * @return com.zihuv.convention.result.Result<?>
     */
    @Operation(summary = "下载账单")
    @GetMapping("/api/pay-service/bill/download")
    public Result<?> downloadBill(@RequestParam String payCode) {
        PayService payService = payStrategyContext.getPayService(payCode);
        payService.billDownload();
        return Result.success();
    }

    /**
     * 来自支付宝的异步通知支付结果（由支付宝调用）
     *
     * @param params 支付宝所通知参数
     * @return 响应字符串 success 或 false
     */
    @Hidden
    @PostMapping("/api/pay-service/notify")
    public String notifyFromAlipay(@RequestParam Map<String, String> params) {
        PayService payService = payStrategyContext.getPayService(PayStrategyEnum.ALIPAY.getPayCode());
        return payService.notifyOrderResult(params);
    }
}