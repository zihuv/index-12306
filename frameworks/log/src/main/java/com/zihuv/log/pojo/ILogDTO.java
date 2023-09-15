package com.zihuv.log.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_logs")
public class ILogDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object parameter;

    /**
     * 请求返回的结果
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object result;
}