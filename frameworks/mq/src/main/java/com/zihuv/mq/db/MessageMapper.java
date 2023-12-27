package com.zihuv.mq.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.mq.domain.LocalMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<LocalMessage> {
}
