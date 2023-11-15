package com.zihuv.payservice.common.enums;

import cn.hutool.core.util.StrUtil;
import com.zihuv.convention.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStrategyEnum {
    WECHAT_PAY("wechat_pay", "weChatPayServiceImpl", "微信支付"),

    ALIPAY("alipay", "aliPayServiceImpl", "支付宝支付");

    /**
     * 支付方式
     */
    private final String payCode;

    /**
     * bean 名称
     */
    private final String className;

    /**
     * 信息
     */
    private final String info;

    public static String getClassNameByPayCode(String payCode) {
        for (PayStrategyEnum strategy : PayStrategyEnum.values()) {
            if (strategy.getPayCode().equals(payCode)) {
                return strategy.getClassName();
            }
        }
        throw new ServiceException(StrUtil.format("不存在 payCode:{} 的支付方式",payCode));
    }
}