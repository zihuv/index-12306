package com.zihuv.userservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.database.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("tb_passenger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 证件类型（eg：中国身份证）
     */
    private Integer idType;

    /**
     * 证件号码
     */
    private String idCard;

    /**
     * 优惠类型（eg：学生票）
     */
    private Integer discountType;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 审核状态
     */
    private Integer verifyStatus;
}