package com.zihuv.log.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ILogDTO {

    @TableId
    private Long id;

    private String module;

    private String name;

    private Integer type;

    private Integer status;

    private String username;

    /**
     * 操作时间
     */
    private LocalDateTime startTime;

    /**
     * 消耗时间
     */
    private Integer spendTime;

    /**
     * URI
     */
    private String uri;

    /**
     * 请求类型
     */
    private String method;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求参数
     */
    private Object parameter;

    /**
     * 请求返回的结果
     */
    private Object result;
}