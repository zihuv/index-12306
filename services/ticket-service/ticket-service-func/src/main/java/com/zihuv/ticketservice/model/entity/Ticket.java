package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_ticket")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private Long trainId;

    private String carriageNumber;

    private String seatNumber;

    private Long passengerId;

    private Integer ticketStatus;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

}