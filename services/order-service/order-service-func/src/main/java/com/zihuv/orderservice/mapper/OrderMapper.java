package com.zihuv.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.orderservice.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}