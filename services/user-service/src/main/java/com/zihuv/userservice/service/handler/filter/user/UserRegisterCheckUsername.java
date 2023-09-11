package com.zihuv.userservice.service.handler.filter.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.enums.UserRegisterErrorCodeEnum;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.service.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserRegisterCheckUsername implements UserRegisterCreateChainFilter<UserRegisterParam>{

    private final UserInfoService userInfoService;

    @Override
    public void handler(UserRegisterParam requestParam) {
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInfo::getUsername,requestParam.getUsername());
        UserInfo userInfo = userInfoService.getOne(lqw);
        if (userInfo != null) {
            throw new ClientException(UserRegisterErrorCodeEnum.HAS_USERNAME_NOTNULL);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}