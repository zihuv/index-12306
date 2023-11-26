package com.zihuv.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.userservice.model.param.UserLoginParam;
import com.zihuv.userservice.model.vo.UserLoginVO;

public interface UserLoginService extends IService<UserLoginParam> {

    UserLoginVO login(UserLoginParam requestParam);

    void logout();

    void checkLogin();
}
