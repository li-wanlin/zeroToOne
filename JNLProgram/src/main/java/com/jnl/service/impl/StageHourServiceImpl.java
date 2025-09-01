package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.StageHour;
import com.jnl.mapper.StageHourMapper;
import com.jnl.service.StageHourService;
import org.springframework.stereotype.Service;

@Service
public class StageHourServiceImpl extends ServiceImpl<StageHourMapper, StageHour> implements StageHourService {
}
