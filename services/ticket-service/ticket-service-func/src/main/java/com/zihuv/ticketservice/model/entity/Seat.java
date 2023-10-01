package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_seat")
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long trainId;

    private String carriageNumber;

    private String seatNumber;

    private Integer seatType;

    private String startStation;

    private String endStation;

    private Integer price;

    private Integer seatStatus;

}