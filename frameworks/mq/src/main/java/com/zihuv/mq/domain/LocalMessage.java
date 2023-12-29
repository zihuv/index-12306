package com.zihuv.mq.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.zihuv.database.base.BaseDO;
import lombok.Data;
import org.springframework.messaging.Message;

@Data
@TableName("tb_message")
public class LocalMessage extends BaseDO {

    @TableId
    private Long id;

    private String destination;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Message<?> messageBody;

    private Integer sendStatus = 0;
}