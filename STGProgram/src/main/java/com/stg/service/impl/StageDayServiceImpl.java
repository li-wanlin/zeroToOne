package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.StageDay;
import com.stg.mapper.StageDayMapper;
import com.stg.service.StageDayService;
import org.springframework.stereotype.Service;

@Service
public class StageDayServiceImpl extends ServiceImpl<StageDayMapper, StageDay> implements StageDayService {
}
