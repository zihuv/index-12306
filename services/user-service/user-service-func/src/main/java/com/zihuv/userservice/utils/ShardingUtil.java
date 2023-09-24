package com.zihuv.userservice.utils;

import static com.zihuv.userservice.common.constant.Index12306Constant.USER_REGISTER_REUSE_SHARDING_COUNT;

public final class ShardingUtil {

    /**
     * 计算分片位置（根据名称分组为 1024 个）
     */
    public static int hashShardingIdx(String name) {
        return Math.abs(name.hashCode() % USER_REGISTER_REUSE_SHARDING_COUNT);
    }
}