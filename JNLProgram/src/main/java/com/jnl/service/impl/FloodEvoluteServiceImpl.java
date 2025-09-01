package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FloodEvolute;
import com.jnl.mapper.FloodEvoluteMapper;
import com.jnl.service.FloodEvoluteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FloodEvoluteServiceImpl extends ServiceImpl<FloodEvoluteMapper, FloodEvolute> implements FloodEvoluteService {


    @Resource
    FloodEvoluteMapper floodEvoluteMapper;


    private static final Logger logger = LoggerFactory.getLogger(FloodEvoluteServiceImpl.class);


    @Override
    public FloodEvolute selectFloodEvolute() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(15);
            QueryWrapper<FloodEvolute> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,now);
            queryWrapper.orderByDesc("time");

            List<FloodEvolute> floodEvolutes = floodEvoluteMapper.selectList(queryWrapper);
            if (floodEvolutes != null && floodEvolutes.size() > 0){
                return floodEvolutes.get(0);
            }
        }catch (Exception e){
            logger.error("");
        }
        return null;
    }
}
