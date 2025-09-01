package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.RealTimeFlow;
import com.jnl.mapper.RealTimeFlowMapper;
import com.jnl.service.RealTimeFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class RealTimeFlowServiceImpl extends ServiceImpl<RealTimeFlowMapper, RealTimeFlow> implements RealTimeFlowService {

    @Resource
    RealTimeFlowMapper realTimeFlowMapper;


    private static final Logger logger = LoggerFactory.getLogger(RealTimeFlowServiceImpl.class);


    @Override
    public RealTimeFlow averageCal() {
        try {
            LocalDate now = LocalDate.now();
            LocalDateTime end = now.atStartOfDay();
            LocalDateTime start = now.minusDays(1).atStartOfDay();


            QueryWrapper<RealTimeFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("date_time",start,end);
            queryWrapper.orderByAsc("date_time");

            List<RealTimeFlow> realTimeFlows = realTimeFlowMapper.selectList(queryWrapper);


            //昨天无数据，直接返回
            if (realTimeFlows == null || realTimeFlows.size() == 0){
                return null;
            }

            //已经计算过，无需计算
            RealTimeFlow flow = realTimeFlows.get(0);
            if (flow.getAverageFlood() != null || flow.getAveragePower() != null){
                return flow;
            }


            OptionalDouble averagePower = realTimeFlows.stream()
                    .filter(realTimeFlow -> realTimeFlow.getPower() != null)
                    .mapToDouble(RealTimeFlow::getPower)
                    .average();

            OptionalDouble averageFlood = realTimeFlows.stream()
                    .filter(realTimeFlow -> realTimeFlow.getFlood() != null)
                    .mapToDouble(RealTimeFlow::getFlood)
                    .average();


            flow.setAveragePower(averagePower.isPresent() ? averagePower.getAsDouble() : null);
            flow.setAverageFlood(averageFlood.isPresent() ? averageFlood.getAsDouble() : null);

            updateById(flow);


            return flow;

        }catch (Exception e){
            logger.error("计算发电流量、泄洪流量日均值发生异常",e);
        }
        return null;
    }



    @Override
    public RealTimeFlow selectRealTimeFlow() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusHours(3);
            QueryWrapper<RealTimeFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("date_time",start,now);
            queryWrapper.orderByDesc("date_time");

            List<RealTimeFlow> realTimeFlows = realTimeFlowMapper.selectList(queryWrapper);
            if (realTimeFlows != null && realTimeFlows.size() > 0){
                return realTimeFlows.get(0);
            }
        }catch (Exception e){
            logger.error("获取发电流量、泄洪流量值发生异常",e);
        }
        return null;
    }
}
