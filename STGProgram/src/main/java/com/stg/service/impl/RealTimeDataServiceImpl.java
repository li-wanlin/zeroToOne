package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.RealTimeData;
import com.stg.mapper.RealTimeDataMapper;
import com.stg.service.RealTimeDataService;
import org.springframework.stereotype.Service;

@Service
public class RealTimeDataServiceImpl extends ServiceImpl<RealTimeDataMapper, RealTimeData> implements RealTimeDataService {
}
