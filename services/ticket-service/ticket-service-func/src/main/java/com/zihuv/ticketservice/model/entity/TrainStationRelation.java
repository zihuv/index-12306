package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.time.LocalDateTime;

import com.zihuv.database.base.BaseDO;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_train_station_relation")
public class TrainStationRelation extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long trainId;

    private String departure;

    private String arrival;

    private String startRegion;

    private String endRegion;

    private Integer departureFlag;

    private Integer arrivalFlag;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

}