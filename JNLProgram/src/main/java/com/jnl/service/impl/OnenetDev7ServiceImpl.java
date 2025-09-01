package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.OnenetDev7;
import com.jnl.mapper.OnenetDev7Mapper;
import com.jnl.service.OnenetDev7Service;
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
public class OnenetDev7ServiceImpl extends ServiceImpl<OnenetDev7Mapper, OnenetDev7> implements OnenetDev7Service {


    @Resource
    OnenetDev7Mapper onenetDev7Mapper;


    
    private static final Logger logger = LoggerFactory.getLogger(OnenetDev7ServiceImpl.class);

    @Override
    public void parseDev7MsgList(List<OnenetParams> pendingDev7List) {

        try{
            if (pendingDev7List.size() == 0){
                return;
            }

            List<OnenetDev7> onenetDev7List = new ArrayList<>(pendingDev7List.size());

            IntStream.range(0, pendingDev7List.size())
                    .parallel()
                    .forEachOrdered(index ->{
                        OnenetParams onenetParams = pendingDev7List.get(index);
                        OnenetDev7 onenetDev7 = new OnenetDev7();
                        onenetDev7.setCsq(onenetParams.getCsq().getValue().intValue());
                        onenetDev7.setVBat(onenetParams.getVbat().getValue());
                        onenetDev7.setTime(onenetParams.getCsq().getTime());
                        onenetDev7.setUpdateTime(DateLocalUtils.parseLongToDate(onenetParams.getCsq().getTime()));
                        if (onenetParams.getLv() != null) {
                            onenetDev7.setLv(onenetParams.getLv().getValue());
                        }
                        if (onenetParams.getLv2() != null) {
                            onenetDev7.setLv2(onenetParams.getLv2().getValue());
                        }
                        if (onenetParams.getLv3() != null) {
                            onenetDev7.setLv3(onenetParams.getLv3().getValue());
                        }
                        onenetDev7List.add(onenetDev7);
                    });

            if (onenetDev7List.size() == 0){
                return;
            }

            boolean saveBatch = saveBatch(onenetDev7List);
        }catch (Exception e){
            logger.error("onenet设备7数据存储发生异常",e);
        }


    }



    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device7");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev7> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev7> devs = onenetDev7Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                unit.setLv(devs.get(0).getLv());
                unit.setLv2(devs.get(0).getLv2());
                unit.setLv3(devs.get(0).getLv3());
                return unit;
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return unit;
    }
}
