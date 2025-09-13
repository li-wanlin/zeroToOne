package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.OnenetDev22;
import com.stg.entity.OnenetDev23;
import com.stg.mapper.OnenetDev23Mapper;
import com.stg.service.OnenetDev23Service;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;
import com.stg.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDev23ServiceImpl extends ServiceImpl<OnenetDev23Mapper, OnenetDev23> implements OnenetDev23Service {


    @Resource
    OnenetDev23Mapper onenetDev23Mapper;

    @Resource
    OverLimitServiceImpl overLimitService;

    private static final Logger logger = LoggerFactory.getLogger(OnenetDev23ServiceImpl.class);


    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device23");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev23> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev23> devs = onenetDev23Mapper.selectList(queryWrapper);
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
