package com.zihuv.userservice.model.param;

import lombok.Data;

@Data
public class UserLoginParam {

    /**
     * 用户名
     */
    private String usernameOrMailOrPhone;

    /**
     * 密码
     */
    private String password;
}