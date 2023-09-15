package com.zihuv.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.log.pojo.ILogDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ILogMapper extends BaseMapper<ILogDTO> {

}