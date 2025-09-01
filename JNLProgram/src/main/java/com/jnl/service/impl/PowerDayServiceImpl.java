package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.PowerDay;
import com.jnl.mapper.PowerDayMapper;
import com.jnl.service.PowerDayService;
import org.springframework.stereotype.Service;


@Service
public class PowerDayServiceImpl extends ServiceImpl<PowerDayMapper, PowerDay> implements PowerDayService {
}
