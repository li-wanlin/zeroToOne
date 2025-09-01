package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FlowHour;
import com.jnl.mapper.FlowHourMapper;
import com.jnl.service.FlowHourService;
import org.springframework.stereotype.Service;


@Service
public class FlowHourServiceImpl extends ServiceImpl<FlowHourMapper, FlowHour> implements FlowHourService {
}
