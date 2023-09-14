package com.zihuv.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.userservice.model.entity.UserInfo;
import jakarta.validation.constraints.NotEmpty;

public interface UserInfoService extends IService<UserInfo> {

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
}
