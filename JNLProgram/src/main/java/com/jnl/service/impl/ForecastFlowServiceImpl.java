package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.ForecastFlow;
import com.jnl.mapper.ForecastFlowMapper;
import com.jnl.service.ForecastFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
public class ForecastFlowServiceImpl extends ServiceImpl<ForecastFlowMapper, ForecastFlow> implements ForecastFlowService {

    @Resource
    ForecastFlowMapper forecastFlowMapper;


    private static final Logger logger = LoggerFactory.getLogger(ForecastFlowServiceImpl.class);


    @Override
    public ForecastFlow selectForecastFlow() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate start = now.minusDays(2);
            QueryWrapper<ForecastFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,now);
            queryWrapper.orderByAsc("time");

            List<ForecastFlow> flows = forecastFlowMapper.selectList(queryWrapper);
            if (flows == null || flows.size() == 0){
                return null;
            }

            ForecastFlow forecastFlow = flows.get(flows.size() - 1);

            return forecastFlow;

        }catch (Exception e){
            logger.error("获取入库流量预报数据失败",e);
        }
        return null;
    }




}
