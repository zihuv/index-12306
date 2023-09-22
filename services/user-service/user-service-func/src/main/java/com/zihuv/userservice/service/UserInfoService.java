package com.zihuv.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.param.UserDeletionParam;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.model.param.UserUpdateParam;
import jakarta.validation.constraints.NotEmpty;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户注册
     *
     * @param requestParam 用户注册入参
     */
    void register(UserRegisterParam requestParam);

    void updateUser(UserUpdateParam userUpdateParam);

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param userId 用户 ID
     * @return 用户详细信息
     */
    UserInfo queryUserByUserId(@NotEmpty String userId);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户详细信息
     */
    UserInfo queryUserByUsername(@NotEmpty String username);

    /**
     * 根据用户 ID 修改用户信息
     *
     * @param requestParam 用户信息入参
     */
    void update(UserInfo requestParam);

    /**
     * 查询是否存在该用户名
     *
     * @param username 用户名
     * @return java.lang.Boolean
     */
    Boolean hasUsername(String username);

    void deletion(UserDeletionParam userDeletionParam);
}
