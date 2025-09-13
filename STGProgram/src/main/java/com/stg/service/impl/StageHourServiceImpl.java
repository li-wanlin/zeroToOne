package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.StageHour;
import com.stg.mapper.StageHourMapper;
import com.stg.service.StageHourService;
import org.springframework.stereotype.Service;

@Service
public class StageHourServiceImpl extends ServiceImpl<StageHourMapper, StageHour> implements StageHourService {
}
