package com.zihuv.log.pojo;

import lombok.Data;

/**
 * ILog 日志打印实体
 */
@Data
public class ILogPrintDTO {

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 请求入参
     */
    private Object[] inputParams;

    /**
     * 返回参数
     */
    private Object outputParams;
}
