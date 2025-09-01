package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.RealTimeData;
import com.jnl.mapper.RealTimeDataMapper;
import com.jnl.service.RealTimeDataService;
import org.springframework.stereotype.Service;

@Service
public class RealTimeDataServiceImpl extends ServiceImpl<RealTimeDataMapper, RealTimeData> implements RealTimeDataService {
}
