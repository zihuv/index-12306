package com.zihuv.payservice.feign;

import com.zihuv.convention.result.Result;
import com.zihuv.payservice.pojo.RefundParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "index12306-pay-service")
public interface PayFeign {

    @PostMapping("/api/pay-service/refund")
    Result<?> refund(@RequestBody RefundParam refundParam);
}
