package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.GNSSDay;
import com.jnl.mapper.GNSSDayMapper;
import com.jnl.service.GNSSDayService;
import org.springframework.stereotype.Service;


@Service
public class GNSSDayServiceImpl extends ServiceImpl<GNSSDayMapper, GNSSDay> implements GNSSDayService {
}
