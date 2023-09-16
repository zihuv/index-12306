package com.zihuv.userservice.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.userservice.mapper.UserLoginMapper;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.enums.UserChainMarkEnum;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.model.vo.UserLoginVO;
import com.zihuv.userservice.service.UserInfoService;
import com.zihuv.userservice.service.UserLoginService;
import com.zihuv.userservice.service.handler.filter.user.UserRegisterCreateChainFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLoginParam> implements UserLoginService {

    private final UserInfoService userInfoService;
    private final AbstractChainContext<UserRegisterParam> abstractChainContext;

    @Override
    public void register(UserRegisterParam requestParam) {
        abstractChainContext.handler(UserChainMarkEnum.USER_REGISTER_FILTER.name(), requestParam);

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(requestParam, userInfo);
        userInfoService.save(userInfo);
    }

    @Override
    public UserLoginVO login(UserLoginParam requestParam) {
        // TODO 校验参数 在数据库查询用户信息并封装
        StpUtil.login(requestParam.getUsernameOrMailOrPhone());

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(requestParam.getUsernameOrMailOrPhone());
        StpUtil.getSession().set(SaSession.USER, userInfo);
        return null;
    }
}