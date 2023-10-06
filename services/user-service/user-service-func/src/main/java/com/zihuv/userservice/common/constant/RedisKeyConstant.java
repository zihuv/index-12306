package com.zihuv.userservice.common.constant;

/**
 * Redis Key 定义常量类
 */
public class RedisKeyConstant {

    /**
     * 用户注销锁
     */
    public static final String USER_DELETION = "index12306-user-service:user-deletion:";

    /**
     * 用户注册可复用用户名分片，Key Prefix + Idx
     */
    public static final String USER_REGISTER_REUSE_SHARDING = "index12306-user-service:user-reuse:";

    /**
     * 用户乘车人列表，Key Prefix + 用户 id
     */
    public static final String USER_PASSENGER_LIST = "index12306-user-service:user-passenger-list:";



    /**
     * 添加乘车人锁，Key Prefix + id card
     */
    public static final String PASSENGER_SAVE_LOCK = "index12306-user-service:passenger_save_lock:";
}