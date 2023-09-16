package com.zihuv.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.model.vo.UserLoginVO;

public interface UserLoginService extends IService<UserLoginParam> {

    /**
     * 用户注册
     *
     * @param requestParam 用户注册入参
     */
    void register(UserRegisterParam requestParam);

    UserLoginVO login(UserLoginParam requestParam);
}
