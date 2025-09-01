package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.OnenetDev6;
import com.jnl.mapper.OnenetDev6Mapper;
import com.jnl.service.OnenetDev6Service;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class OnenetDev6ServiceImpl extends ServiceImpl<OnenetDev6Mapper, OnenetDev6> implements OnenetDev6Service {


    @Resource
    OnenetDev6Mapper onenetDev6Mapper;

    @Resource
    OverLimitServiceImpl overLimitService;

    
    private static final Logger logger = LoggerFactory.getLogger(OnenetDev6ServiceImpl.class);
    
    
    @Override
    public void parseDev6MsgList(List<OnenetParams> pendingDev6List) {

        try{
            if (pendingDev6List.size() == 0){
                return;
            }

            List<OnenetDev6> onenetDev6List = new ArrayList<>(pendingDev6List.size());

            IntStream.range(0, pendingDev6List.size())
                    .parallel()
                    .forEachOrdered(index ->{
                        OnenetParams onenetParams = pendingDev6List.get(index);
                        OnenetDev6 onenetDev6 = new OnenetDev6();
                        onenetDev6.setCsq(onenetParams.getCsq().getValue().intValue());
                        onenetDev6.setVBat(onenetParams.getVbat().getValue());
                        onenetDev6.setTime(onenetParams.getCsq().getTime());
                        onenetDev6.setUpdateTime(DateLocalUtils.parseLongToDate(onenetParams.getCsq().getTime()));
                        if (onenetParams.getLv() != null) {
                            onenetDev6.setLv(onenetParams.getLv().getValue());
                        }
                        onenetDev6List.add(onenetDev6);
                    });

            if (onenetDev6List.size() == 0){
                return;
            }

            overLimitService.saveDeviceSix(onenetDev6List);

            boolean saveBatch = saveBatch(onenetDev6List);
        }catch (Exception e){
            logger.error("onenet设备6数据存储发生异常",e);
        }


    }



    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device6");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev6> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev6> devs = onenetDev6Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                unit.setLv(devs.get(0).getLv());
                return unit;
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return unit;
    }
}
