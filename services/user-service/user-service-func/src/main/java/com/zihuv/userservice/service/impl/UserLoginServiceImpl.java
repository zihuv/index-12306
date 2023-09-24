package com.zihuv.userservice.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.userservice.mapper.UserLoginMapper;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.model.vo.UserLoginVO;
import com.zihuv.userservice.service.UserInfoService;
import com.zihuv.userservice.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLoginParam> implements UserLoginService {

    private final UserInfoService userInfoService;
    private final RedissonClient redissonClient;
    private final AbstractChainContext<UserRegisterParam> abstractChainContext;


    @Override
    public UserLoginVO login(UserLoginParam userLoginParam) {
        // TODO 校验参数 在数据库查询用户信息并封装
        UserInfo userInfo = new UserInfo();
        String usernameOrMailOrPhone = userLoginParam.getUsernameOrMailOrPhone();
        if (isUsername(usernameOrMailOrPhone)) {
            userInfo = userInfoService.queryUserByUsername(userLoginParam.getUsernameOrMailOrPhone());
        } else if (isEmail(usernameOrMailOrPhone)) {
            System.out.println("这是一个邮箱地址");
        } else if (isPhoneNumber(usernameOrMailOrPhone)) {
            System.out.println("这是一个手机号码");
        } else {
            System.out.println("未知类型");
        }

        if (userInfo.getId() == null) {
            throw new ClientException("查询不到该用户");
        }
        StpUtil.login(userInfo.getId());
        StpUtil.getSession().set(SaSession.USER, userInfo);
        return null;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public void checkLogin() {
        StpUtil.checkLogin();
    }

    // 校验是否是用户名（字母和数字组合，长度在3到20之间）
    public static boolean isUsername(String input) {
        String regex = "^[a-zA-Z0-9]{3,20}$";
        return input.matches(regex);
    }

    // 校验是否是邮箱地址
    public static boolean isEmail(String input) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return input.matches(regex);
    }

    // 校验是否是手机号码（中国大陆手机号，11位数字，以1开头）
    public static boolean isPhoneNumber(String input) {
        String regex = "^1[0-9]{10}$";
        return input.matches(regex);
    }
}