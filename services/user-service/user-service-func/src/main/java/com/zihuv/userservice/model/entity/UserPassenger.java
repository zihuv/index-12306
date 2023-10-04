package com.zihuv.userservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.database.base.BaseDO;
import lombok.Data;

@TableName("tb_user_passenger")
@Data
public class UserPassenger extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private Long passengerId;
}