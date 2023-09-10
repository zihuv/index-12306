package com.zihuv.userservice.controller;

import com.zihuv.common.pojo.Result;
import com.zihuv.userservice.service.UserInfoService;

import com.zihuv.web.handler.GlobalExceptionHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/api/user-service/query")
    public Result<?> queryUserByUsername(@RequestParam("username") @NotBlank(message = "用户名不能为空")  String username) {
        return Result.ok(userInfoService.queryUserByUsername(username));
    }

}