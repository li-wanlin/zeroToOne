package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.HistoryFlow;
import com.stg.mapper.HistoryFlowMapper;
import com.stg.service.HistoryFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoryFlowServiceImpl extends ServiceImpl<HistoryFlowMapper, HistoryFlow> implements HistoryFlowService {

    @Resource
    HistoryFlowMapper historyFlowMapper;

    private static final Logger logger = LoggerFactory.getLogger(HistoryFlowServiceImpl.class);


    public Map<String,Double> fiveDayData(){
        try {
            LocalDateTime now = LocalDateTime.now();

            LocalDate oneDay = LocalDate.now().minusDays(1);
            LocalDate fiveDay = LocalDate.now().minusDays(5);
            LocalDateTime start = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            Map<String, Double> map = new HashMap<>();

            QueryWrapper<HistoryFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",fiveDay,oneDay);
            queryWrapper.orderByAsc("time");

            List<HistoryFlow> historyFlows = historyFlowMapper.selectList(queryWrapper);
            if (historyFlows == null || historyFlows.size() != 5){
                return null;
            }



            map.put("oneDayFlow",historyFlows.get(0).getFlow());
            map.put("twoDayFlow",historyFlows.get(1).getFlow());
            map.put("threeDayFlow",historyFlows.get(2).getFlow());
            map.put("fourDayFlow",historyFlows.get(3).getFlow());
            map.put("fiveDayFlow",historyFlows.get(4).getFlow());

            return map;
        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



}
