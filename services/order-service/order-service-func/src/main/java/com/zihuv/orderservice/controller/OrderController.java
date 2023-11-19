package com.zihuv.orderservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "订单管理")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 车票订单创建，返回订单号
     */
    @Operation(summary = "车票订单创建，返回订单号")
    @PostMapping("/api/order-service/order/create")
    public Result<String> createOrder(@RequestBody TicketOrderCreateParam requestParam) {
        String orderNo = orderService.createOrder(requestParam);
        return Result.success(orderNo);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PostMapping("/api/order-service/order/cancel")
    public Result<?> cancelOrder(@RequestBody TicketOrderCreateParam requestParam) {
        orderService.cancelOrder(requestParam);
        return Result.success();
    }

    /**
     * 查询订单
     */
    @Operation(summary = "查询订单")
    @GetMapping("/api/order-service/order/query")
    public Result<?> queryOrder(@RequestBody TicketOrderCreateParam requestParam) {
        orderService.queryOrder(requestParam);
        return Result.success();
    }
}