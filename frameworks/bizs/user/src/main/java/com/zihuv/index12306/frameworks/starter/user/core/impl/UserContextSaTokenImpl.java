package com.zihuv.index12306.frameworks.starter.user.core.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.index12306.frameworks.starter.user.core.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextSaTokenImpl implements UserContext {

    private final ObjectMapper objectMapper;

    @Override
    public String getUsername() {
        try {
            String userInfoJson = objectMapper.writeValueAsString(StpUtil.getSession().get(SaSession.USER));
            UserInfoDTO userInfo = objectMapper.readValue(userInfoJson, UserInfoDTO.class);
            return userInfo != null ? userInfo.getUsername() : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}