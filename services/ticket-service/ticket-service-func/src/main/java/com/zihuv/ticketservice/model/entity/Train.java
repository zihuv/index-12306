package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.zihuv.database.base.BaseDO;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 列车
 */
@Data
@TableName("tb_train")
public class Train extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 列车车次
     */
    private String trainNumber;

    /**
     * 列车类型 0：高铁 1：动车 2：普通车
     */
    private Integer trainType;

    /**
     * 列车标签 0：复兴号 1：智能动车组 2：静音车厢 3：支持选铺
     */
    private String trainTag;

    /**
     * 列车品牌类型 0：GC-高铁/城际 1：D-动车 2：Z-直达 3：T-特快 4：K-快速 5：其他 6：复兴号 7：智能动车组
     */
    private String trainBrand;

    /**
     * 起始站
     */
    private String startStation;

    /**
     * 终点站
     */
    private String endStation;

    /**
     * 起始城市
     */
    private String startRegion;

    /**
     * 终点城市
     */
    private String endRegion;

    /**
     * 销售时间
     */
    private LocalDateTime saleTime;

    /**
     * 销售状态 0：可售 1：不可售 2：未知
     */
    private Integer saleStatus;

    /**
     * 出发时间
     */
    private LocalDateTime departureTime;

    /**
     * 到达时间
     */
    private LocalDateTime arrivalTime;


}