package com.zihuv.orderservice.feign;

import com.zihuv.convention.result.Result;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "index12306-order-service")
public interface OrderFeign {

    /**
     * 创建车票订单，返回订单号
     */
    @PostMapping("/api/order-service/order/create")
    Result<String> createOrder(@RequestBody TicketOrderCreateParam requestParam);

    /**
     * 关闭订单
     */
    @PostMapping("/api/order-service/order/close")
    Result<?> closeOrder(@RequestBody String orderNo);
}