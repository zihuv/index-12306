package com.zihuv.userservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.database.base.BaseDO;
import lombok.Data;

@TableName(value = "tb_user")
@Data
public class UserInfo extends BaseDO {

    @TableId
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 国家/地区
     */
    private String region;

    /**
     * 证件类型
     */
    private Integer idType;

    /**
     * 证件号
     */
    //@JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String idCard;

    /**
     * 手机号
     */
    //@JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 旅客类型
     */
    private Integer userType;

    /**
     * 审核状态
     */
    private Integer verifyStatus;

    /**
     * 地址
     */
    private String address;

    /**
     * 邮编
     */
    private String postCode;

}