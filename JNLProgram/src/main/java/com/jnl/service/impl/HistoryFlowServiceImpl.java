package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.ForecastFlow;
import com.jnl.entity.HistoryFlow;
import com.jnl.mapper.HistoryFlowMapper;
import com.jnl.service.HistoryFlowService;
import com.jnl.utils.DateLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistoryFlowServiceImpl extends ServiceImpl<HistoryFlowMapper, HistoryFlow> implements HistoryFlowService {

    @Resource
    HistoryFlowMapper historyFlowMapper;

    private static final Logger logger = LoggerFactory.getLogger(HistoryFlowServiceImpl.class);


    public ForecastFlow fiveDayData(){
        try {
            ForecastFlow forecastFlow = new ForecastFlow();
            LocalDateTime now = LocalDateTime.now();
            LocalDate nowDay = LocalDate.now();

            LocalDate oneDay = nowDay.minusDays(1);
            LocalDate fiveDay = nowDay.minusDays(5);
            LocalDateTime start = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            Map<String, Double> map = new HashMap<>();

            QueryWrapper<HistoryFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",fiveDay,oneDay);
            queryWrapper.orderByAsc("time");

            List<HistoryFlow> historyFlows = historyFlowMapper.selectList(queryWrapper);
            if (historyFlows == null){
                forecastFlow.setOneDayFlow(null);
                forecastFlow.setTwoDayFlow(null);
                forecastFlow.setThreeDayFlow(null);
                forecastFlow.setFourDayFlow(null);
                forecastFlow.setFiveDayFlow(null);

                return forecastFlow;
            }


            Map<Date, Double> doubleMap = historyFlows.stream().collect(Collectors.toMap(HistoryFlow::getTime, HistoryFlow::getFlow));
            String[] keys = {"oneDayFlow", "twoDayFlow", "threeDayFlow", "fourDayFlow", "fiveDayFlow"};


            for (int i = 0; i < 5; i++) {
                LocalDate calDay = nowDay.minusDays(i+1);
                map.put(keys[i],doubleMap.getOrDefault(DateLocalUtils.parseLocalDateToDateStart(calDay),null));
            }

            forecastFlow.setOneDayFlow(map.get("oneDayFlow"));
            forecastFlow.setTwoDayFlow(map.get("twoDayFlow"));
            forecastFlow.setThreeDayFlow(map.get("threeDayFlow"));
            forecastFlow.setFourDayFlow(map.get("fourDayFlow"));
            forecastFlow.setFiveDayFlow(map.get("fiveDayFlow"));



            return forecastFlow;
        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



}
