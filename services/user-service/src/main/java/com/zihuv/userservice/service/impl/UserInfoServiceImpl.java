package com.zihuv.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.userservice.mapper.UserInfoMapper;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.service.UserInfoService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo queryUserByUserId(String userId) {
        UserInfo userInfo = this.getById(userId);
        if (Objects.isNull(userInfo)) {
            throw new ClientException("该用户不存在");
        }
        return userInfo;
    }

    @Override
    public UserInfo queryUserByUsername(String username) {
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInfo::getUsername,username);
        UserInfo userInfo = this.getOne(lqw);
        if (Objects.isNull(userInfo)) {
            throw new ClientException("该用户不存在");
        }
        return userInfo;
    }

    @Override
    public void update(UserInfo requestParam) {

    }

    @Override
    public Boolean hasUsername(String username) {
        // TODO 使用缓存查询该用户名是否存在
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInfo::getUsername,username);
        UserInfo userInfo = this.getOne(lqw);
        return Objects.nonNull(userInfo);
    }
}