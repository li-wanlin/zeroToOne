package com.stg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.stg.entity.PitfallCheck;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckMapper extends BaseMapper<PitfallCheck> {
}
