package com.zihuv.userservice.model.param;

import lombok.Data;

@Data
public class PassengerParam {

    /**
     * 乘车人id
     */
    private String passengerId;

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
     * 优惠类型
     */
    private Integer discountType;

    /**
     * 手机号
     */
    private String phone;
}