package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.PerDelimit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LimitMapper extends BaseMapper<PerDelimit> {
}
