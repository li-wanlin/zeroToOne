package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.AverageFlow;
import com.stg.mapper.AverageFlowMapper;
import com.stg.service.AverageFlowService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.matlabVo.ResUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AverageFlowServiceImpl extends ServiceImpl<AverageFlowMapper, AverageFlow> implements AverageFlowService {

    @Resource
    AverageFlowMapper averageFlowMapper;

    private static final Logger logger = LoggerFactory.getLogger(AverageFlowServiceImpl.class);


    @Override
    public ResUnit selectAverageFlow() {
        try {
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime start = nowHour.minusHours(2);
            QueryWrapper<AverageFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,nowHour);
            queryWrapper.orderByDesc("time");

            List<AverageFlow> averageFlows = averageFlowMapper.selectList(queryWrapper);
            if (averageFlows == null || averageFlows.size() == 0){
                return null;
            }

            ResUnit unit = new ResUnit();
            unit.setTime(DateLocalUtils.parseDateToStr(averageFlows.get(0).getTime()));
            unit.setValue(averageFlows.get(0).getFlow());
            return unit;

        }catch (Exception e){
            logger.error("获取小时平均来流量发生异常",e);
        }

        return null;
    }
}
