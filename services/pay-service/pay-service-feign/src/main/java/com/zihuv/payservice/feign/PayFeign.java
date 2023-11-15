package com.zihuv.payservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "index12306-pay-service")
public interface PayFeign {
}
