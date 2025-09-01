package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.AverageFlow;
import com.jnl.mapper.AverageFlowMapper;
import com.jnl.service.AverageFlowService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.matlabVo.ResUnit;
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
    public AverageFlow selectAverageFlow() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusHours(3);
            QueryWrapper<AverageFlow> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,now);
            queryWrapper.orderByDesc("time");

            List<AverageFlow> averageFlows = averageFlowMapper.selectList(queryWrapper);
            if (averageFlows == null || averageFlows.size() == 0){
                return null;
            }

            return averageFlows.get(0);

        }catch (Exception e){
            logger.error("获取小时平均来流量发生异常",e);
        }

        return null;
    }
}
