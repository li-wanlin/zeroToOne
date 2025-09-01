package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.DeviceCycle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CycleMapper extends BaseMapper<DeviceCycle> {
}
