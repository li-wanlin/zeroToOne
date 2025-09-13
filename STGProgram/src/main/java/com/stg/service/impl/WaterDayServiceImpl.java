package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.WaterDay;
import com.stg.mapper.WaterDayMapper;
import com.stg.service.WaterDayService;
import org.springframework.stereotype.Service;

@Service
public class WaterDayServiceImpl extends ServiceImpl<WaterDayMapper, WaterDay> implements WaterDayService {
}
