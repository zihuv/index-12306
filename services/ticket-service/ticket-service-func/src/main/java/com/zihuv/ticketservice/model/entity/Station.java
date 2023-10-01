package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_station")
public class Station implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String name;

    private String spell;

    private String region;

    private String regionName;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

}