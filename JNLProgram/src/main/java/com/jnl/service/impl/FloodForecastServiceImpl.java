package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FloodForecast;
import com.jnl.mapper.FloodForecastMapper;
import com.jnl.service.FloodForecastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FloodForecastServiceImpl extends ServiceImpl<FloodForecastMapper, FloodForecast> implements FloodForecastService {

    @Resource
    FloodForecastMapper floodForecastMapper;


    private static final Logger logger = LoggerFactory.getLogger(FloodForecastServiceImpl.class);


    @Override
    public FloodForecast selectFloodForecast() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(15);
            QueryWrapper<FloodForecast> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,now);
            queryWrapper.orderByDesc("time");

            List<FloodForecast> floodForecasts = floodForecastMapper.selectList(queryWrapper);
            if (floodForecasts != null && floodForecasts.size() > 0){
                return floodForecasts.get(0);
            }
        }catch (Exception e){
            logger.error("");
        }
        return null;
    }
}
