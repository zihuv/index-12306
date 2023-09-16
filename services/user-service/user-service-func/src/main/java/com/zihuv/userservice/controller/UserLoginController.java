package com.zihuv.userservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.vo.UserLoginVO;
import com.zihuv.userservice.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserLoginController {

    private final UserLoginService userLoginService;
    private final UserContext userContext;

    /**
     * 用户登录
     */
    @PostMapping("/api/user-service/v1/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginParam requestParam) {
        return Result.success(userLoginService.login(requestParam));
    }
}