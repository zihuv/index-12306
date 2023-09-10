package com.zihuv.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.userservice.model.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}