package com.zihuv.userservice.utils;

import static com.zihuv.userservice.common.constant.Index12306Constant.USER_REGISTER_REUSE_SHARDING_COUNT;

public final class UserReuseUtil {

    /**
     * 计算分片位置（将用户名进行分组为 1024 个）
     */
    public static int hashShardingIdx(String username) {
        return Math.abs(username.hashCode() % USER_REGISTER_REUSE_SHARDING_COUNT);
    }
}