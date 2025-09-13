package com.stg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.GNSSDay;
import com.stg.mapper.GNSSDayMapper;
import com.stg.service.GNSSDayService;
import org.springframework.stereotype.Service;


@Service
public class GNSSDayServiceImpl extends ServiceImpl<GNSSDayMapper, GNSSDay> implements GNSSDayService {
}
