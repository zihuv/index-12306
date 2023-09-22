package com.zihuv.userservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.vo.UserLoginVO;
import com.zihuv.userservice.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户登录")
@Validated
@RestController
@RequiredArgsConstructor
public class UserLoginController {

    private final UserLoginService userLoginService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/api/user-service/v1/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginParam requestParam) {
        return Result.success(userLoginService.login(requestParam));
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @GetMapping("/api/user-service/v1/logout")
    public Result<?> logout() {
        userLoginService.logout();
        return Result.success();
    }
}