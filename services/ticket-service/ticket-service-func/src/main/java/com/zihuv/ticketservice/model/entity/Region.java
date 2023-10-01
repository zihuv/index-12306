package com.zihuv.ticketservice.model.entity;

import java.io.Serializable;

import java.util.Date;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("tb_region")
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String fullName;

    private String code;

    private String initial;

    private String spell;

    private Integer popularFlag;

    private Date createTime;

    private Date updateTime;

    private Integer delFlag;

}