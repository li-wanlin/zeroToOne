package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.RainDay;
import com.jnl.mapper.RainDayMapper;
import com.jnl.service.RainDayService;
import org.springframework.stereotype.Service;

@Service
public class RainDayServiceImpl extends ServiceImpl<RainDayMapper, RainDay> implements RainDayService {
}
