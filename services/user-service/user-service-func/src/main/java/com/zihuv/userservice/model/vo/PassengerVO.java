package com.zihuv.userservice.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zihuv.userservice.serialize.IdCardDesensitizationSerializer;
import com.zihuv.userservice.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 乘车人 VO
 * #{@link com.zihuv.userservice.pojo.PassengerVO}
 */
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
    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
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
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 真实手机号
     */
    private String actualPhone;

    /**
     * 添加日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime createDate;

    /**
     * 审核状态
     */
    private Integer verifyStatus;
}