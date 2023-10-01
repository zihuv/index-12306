package com.zihuv.ticketservice.common.constant;


/**
 * Redis Key 定义常量类
 */
public class RedisKeyConstant {

    /**
     * 所有车站信息缓存
     */
    public static final String STATION_INFO_ALL = "index12306-ticket-service:all_station";

    /**
     * id 为 xxx 车站信息缓存，Key Prefix + 车站ID
     */
    public static final String STATION_INFO = "index12306-ticket-service:station:";

    /**
     * 列车基本信息，Key Prefix + 列车 ID
     */
    public static final String TRAIN_INFO = "index12306-ticket-service:train_info:";

    /**
     * 列车路线信息查询，Key Prefix + 列车ID
     */
    public static final String TRAIN_STATION_STOPOVER_DETAIL = "index12306-ticket-service:train_station_stopover_detail:";

    /**
     * 列车购买令牌桶，Key Prefix + 列车ID
     */
    public static final String TICKET_AVAILABILITY_TOKEN_BUCKET = "index12306-ticket-service:ticket_availability_token_bucket:";

    /**
     * 列车购买令牌桶加载数据 Key
     */
    public static final String LOCK_TICKET_AVAILABILITY_TOKEN_BUCKET = "index12306-ticket-service:lock:ticket_availability_token_bucket:%s";
}