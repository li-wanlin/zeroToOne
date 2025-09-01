package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.FloodForecast;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FloodForecastMapper extends BaseMapper<FloodForecast> {
}
