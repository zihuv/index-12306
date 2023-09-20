package com.zihuv.userservice.controller;

import com.zihuv.DistributedCache;
import com.zihuv.convention.result.Result;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.log.annotation.ILog;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.model.param.UserUpdateParam;
import com.zihuv.userservice.service.UserInfoService;
import com.zihuv.userservice.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户信息")
@Validated
@RestController
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserLoginService userLoginService;

    @ILog
    @Operation(summary = "根据用户名查询用户信息")
    @Parameter(name = "username", description = "用户名")
    @GetMapping("/api/user-service/query")
    public Result<UserInfo> queryUserByUsername(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username) {
        return Result.success(userInfoService.queryUserByUsername(username));
    }

    @Operation(summary = "检查用户名是否已存在 true-存在 false-不存在")
    @GetMapping("/api/user-service/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") @NotBlank(message = "用户名不能为空") String username) {
        return Result.success(userInfoService.hasUsername(username));
    }

    @Operation(summary = "注册用户")
    @PostMapping("/api/user-service/register")
    public Result<?> register(@RequestBody @Valid UserRegisterParam userRegisterParam) {
        userLoginService.register(userRegisterParam);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @Idempotent
    @PostMapping("/api/user-service/update")
    public Result<?> update(@RequestBody UserUpdateParam userUpdateParam) {
        userLoginService.updateUser(userUpdateParam);
        return Result.success();
    }

}