package com.zihuv.orderservice.feign;

import com.zihuv.convention.result.Result;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "index12306-order-service")
public interface OrderFeign {

    @PostMapping("/api/order-service/order/create")
    Result<?> createOrder(@RequestBody TicketOrderCreateParam requestParam);
}