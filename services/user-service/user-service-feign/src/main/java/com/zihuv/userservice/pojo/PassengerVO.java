package com.zihuv.userservice.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PassengerVO {

    /**
     * 乘车人id
     */
    private String passengerId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 证件类型
     */
    private Integer idType;

    /**
     * 证件号码
     */
    private String idCard;

    /**
     * 真实证件号码
     */
    private String actualIdCard;

    /**
     * 优惠类型
     */
    private Integer discountType;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 真实手机号
     */
    private String actualPhone;

    /**
     * 添加日期
     */
    private LocalDateTime createDate;

    /**
     * 审核状态
     */
    private Integer verifyStatus;
}