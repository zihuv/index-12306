package com.zihuv.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.userservice.model.param.UserLoginParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoginMapper extends BaseMapper<UserLoginParam> {
}