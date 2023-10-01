package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_carriage")
public class Carriage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long trainId;

    private String carriageNumber;

    private Integer carriageType;

    private Integer seatCount;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

}