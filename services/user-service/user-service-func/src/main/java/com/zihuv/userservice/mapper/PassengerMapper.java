package com.zihuv.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.userservice.model.entity.Passenger;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PassengerMapper extends BaseMapper<Passenger> {
}