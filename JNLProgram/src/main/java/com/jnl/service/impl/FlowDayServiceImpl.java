package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FlowDay;
import com.jnl.mapper.FlowDayMapper;
import com.jnl.service.FlowDayService;
import org.springframework.stereotype.Service;


@Service
public class FlowDayServiceImpl extends ServiceImpl<FlowDayMapper, FlowDay> implements FlowDayService {
}
