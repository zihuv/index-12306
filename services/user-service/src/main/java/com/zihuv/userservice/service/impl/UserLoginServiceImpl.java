package com.zihuv.userservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.userservice.mapper.UserLoginMapper;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.service.UserInfoService;
import com.zihuv.userservice.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLoginParam> implements UserLoginService {

    private final UserInfoService userInfoService;

    @Override
    public void register(UserRegisterParam requestParam) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(requestParam.getUsername());
        userInfoService.save(userInfo);
    }
}