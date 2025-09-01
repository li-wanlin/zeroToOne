package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.jnl.entity.FloodControl;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FloodControlMapper extends BaseMapper<FloodControl> {
}
