package com.zihuv.convention.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志枚举类
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 查询
     */
    GET(1),

    /**
     * 新增
     */
    CREATE(2),

    /**
     * 修改
     */
    UPDATE(3),

    /**
     * 删除
     */
    DELETE(4),

    /**
     * 导出
     */
    EXPORT(5),

    /**
     * 导入
     */
    IMPORT(6),

    /**
     * 其它（在无法归类时，可以选择使用其它。因为还有操作名可以进一步标识）
     */
    OTHER(0),

    /**
     * null
     */
    NULL(-1);

    /**
     * 类型
     */
    private final Integer type;
}
