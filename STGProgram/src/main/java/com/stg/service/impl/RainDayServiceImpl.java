package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.RainDay;
import com.stg.mapper.RainDayMapper;
import com.stg.service.RainDayService;
import org.springframework.stereotype.Service;

@Service
public class RainDayServiceImpl extends ServiceImpl<RainDayMapper, RainDay> implements RainDayService {
}
