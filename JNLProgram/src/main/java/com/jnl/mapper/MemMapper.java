package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.ProMemoirs;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemMapper extends BaseMapper<ProMemoirs> {
}
