package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.WaterHour;
import com.stg.mapper.WaterHourMapper;
import com.stg.service.WaterHourService;
import org.springframework.stereotype.Service;


@Service
public class WaterHourServiceImpl extends ServiceImpl<WaterHourMapper, WaterHour> implements WaterHourService {
}
