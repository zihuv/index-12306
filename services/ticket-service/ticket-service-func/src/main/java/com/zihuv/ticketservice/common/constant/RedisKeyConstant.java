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
     * 获取全部地点集合 Key
     */
    public static final String QUERY_ALL_REGION_LIST = "index12306-ticket-service:query_all_region_list";

    /**
     * 地区与站点映射查询
     */
    public static final String REGION_TRAIN_STATION_MAPPING = "index12306-ticket-service:region_train_station_mapping";

    /**
     * 车站 code 和 id 映射查询
     */
    public static final String STATION_CODE_ID_MAPPING = "index12306-ticket-service:station_code_id_mapping";

    /**
     * 获取全部地点集合分布式锁 Key
     */
    public static final String LOCK_QUERY_ALL_REGION_LIST = "index12306-ticket-service:lock:query_all_region_list";

    /**
     * id 为 xxx 列车经过的车站信息缓存，Key Prefix + 列车ID（列车经过的车站）
     */
    public static final String TRAIN_PASS_STATION_INFO = "index12306-ticket-service:station:train_id:";

    /**
     * id 为 xxx 列车车站信息缓存，Key Prefix + 车站ID（车站经过的列车）
     */
    public static final String STATION_TRAIN_PASS_SET = "index12306-ticket-service:station:station_id_train_id_mapping:";

    /**
     * 列车基本信息，Key Prefix + 列车 ID
     */
    public static final String TRAIN_INFO = "index12306-ticket-service:train_info:";

    /**
     * 车站基本信息，Key Prefix + 车站 CODE
     */
    public static final String TRAIN_STATION_INFO = "index12306-ticket-service:station_info:";

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

    /**
     * 列车座位空余数据，列车id:{}:路线:{}:车厢:{}:座位字母:{}-座位
     *
     */
    public static final String SEAT_BUCKET = "index12306-ticket-service:seat_bucket:train_id:{}:route:{}:carriage:{}:seat_letter:{}";
}