package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.StageDay;
import com.jnl.mapper.StageDayMapper;
import com.jnl.service.StageDayService;
import org.springframework.stereotype.Service;

@Service
public class StageDayServiceImpl extends ServiceImpl<StageDayMapper, StageDay> implements StageDayService {
}
