package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.FloodPrevent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FloodMapper extends BaseMapper<FloodPrevent> {
}
