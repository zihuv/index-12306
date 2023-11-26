package com.zihuv.userservice.feign;

import com.zihuv.convention.result.Result;
import com.zihuv.userservice.pojo.PassengerVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "index12306-user-service")
public interface UserPassengerFeign {

    @GetMapping("/api/user-service/passenger/query")
    Result<List<PassengerVO>> listPassengerVO(@RequestParam("userId") String userId);
}
