package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.PitfallCheck;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckMapper extends BaseMapper<PitfallCheck> {
}
