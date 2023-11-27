package com.zihuv.orderservice.controller;

import com.zihuv.orderservice.model.vo.OrderVO;
import com.zihuv.convention.result.Result;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建车票订单，返回订单号（feign）
     */
//    @Idempotent(
//            uniqueKeyPrefix = IdempotentConstant.PURCHASE_TICKETS,
//            key = USER_CONTEXT_SPEL,
//            message = "正在执行下单流程，请稍后...",
//            scene = IdempotentSceneEnum.RESTAPI,
//            type = IdempotentTypeEnum.SPEL
//    )
    //TODO 不需要返回订单号，把消息交给消息队列即可
    @Operation(summary = "创建车票订单，返回订单号")
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
    public Result<?> cancelOrder(@RequestBody String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }

    /**
     * 关闭订单
     */
    @Operation(summary = "关闭订单")
    @PostMapping("/api/order-service/order/close")
    public Result<?> closeOrder(@RequestBody String orderNo) {
        orderService.closeOrder(orderNo);
        return Result.success();
    }

    /**
     * 查询订单
     *
     * @param orderNo 订单号
     */
    @Operation(summary = "查询订单")
    @GetMapping("/api/order-service/order/query")
    public Result<OrderVO> queryOrder(@RequestParam String orderNo) {
        OrderVO orderVO = orderService.queryOrder(orderNo);
        return Result.success(orderVO);
    }
}