package com.zihuv.database.handler;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        Snowflake snowflake = IdUtil.getSnowflake();
        return snowflake.nextId();
    }
}