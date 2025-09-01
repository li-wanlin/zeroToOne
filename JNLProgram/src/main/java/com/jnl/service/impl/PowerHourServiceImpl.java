package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.PowerHour;
import com.jnl.mapper.PowerHourMapper;
import com.jnl.service.PowerHourService;
import org.springframework.stereotype.Service;

@Service
public class PowerHourServiceImpl extends ServiceImpl<PowerHourMapper, PowerHour> implements PowerHourService {
}
