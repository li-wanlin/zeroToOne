package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.SeepageDay;
import com.jnl.mapper.SeepageDayMapper;
import com.jnl.service.SeepageDayService;
import org.springframework.stereotype.Service;


@Service
public class SeepageDayServiceImpl extends ServiceImpl<SeepageDayMapper, SeepageDay> implements SeepageDayService {
}
