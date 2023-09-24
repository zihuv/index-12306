package com.zihuv.userservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("tb_user_passenger")
@Data
public class UserPassenger {

    @TableId
    private Long id;

    private Long userId;

    private Long passengerId;
}