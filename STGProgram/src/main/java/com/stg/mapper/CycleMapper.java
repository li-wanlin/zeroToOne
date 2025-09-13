package com.stg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.stg.entity.DeviceCycle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CycleMapper extends BaseMapper<DeviceCycle> {
}
