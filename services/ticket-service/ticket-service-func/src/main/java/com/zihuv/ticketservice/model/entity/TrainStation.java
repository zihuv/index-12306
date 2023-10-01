package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.zihuv.database.base.BaseDO;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 站点
 */
@Data
@TableName("tb_train_station")
public class TrainStation extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 车次id
     */
    private Long trainId;

    /**
     * 车站id
     */
    private Long stationId;

    /**
     * 站点顺序
     */
    private String sequence;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;

    /**
     * 起始城市
     */
    private String startRegion;

    /**
     * 终点城市
     */
    private String endRegion;

    /**
     * 到站时间
     */
    private LocalDateTime arrivalTime;

    /**
     * 出站时间
     */
    private LocalDateTime departureTime;

    /**
     * 停留时间，单位分
     */
    private Integer stopoverTime;
}