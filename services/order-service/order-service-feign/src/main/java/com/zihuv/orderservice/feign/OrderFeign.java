package com.zihuv.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "index12306-order-service")
public interface OrderFeign {
}