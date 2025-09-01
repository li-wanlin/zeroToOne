package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.SeepageHour;
import com.jnl.mapper.SeepageHourMapper;
import com.jnl.service.SeepageHourService;
import org.springframework.stereotype.Service;


@Service
public class SeepageHourServiceImpl extends ServiceImpl<SeepageHourMapper, SeepageHour> implements SeepageHourService {
}
