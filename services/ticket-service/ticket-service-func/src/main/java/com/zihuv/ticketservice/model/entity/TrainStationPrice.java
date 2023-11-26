package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import com.zihuv.database.base.BaseDO;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_train_station_price")
public class TrainStationPrice extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long trainId;

    private String departure;

    private String arrival;

    private Integer seatType;

    private Integer price;

}