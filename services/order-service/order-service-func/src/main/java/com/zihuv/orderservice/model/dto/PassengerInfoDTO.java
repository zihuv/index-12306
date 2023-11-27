package com.zihuv.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 乘车人购票信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerInfoDTO {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;
}