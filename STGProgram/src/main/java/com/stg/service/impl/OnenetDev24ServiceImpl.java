package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.OnenetDev23;
import com.stg.entity.OnenetDev24;
import com.stg.mapper.OnenetDev24Mapper;
import com.stg.service.OnenetDev24Service;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;
import com.stg.vo.onenetVo.WaterInfoResponse;
import com.stg.vo.onenetVo.WaterUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDev24ServiceImpl extends ServiceImpl<OnenetDev24Mapper, OnenetDev24> implements OnenetDev24Service {

    @Resource
    OnenetDev24Mapper onenetDev24Mapper;


    @Resource
    OverLimitServiceImpl overLimitService;


    private static final Logger logger = LoggerFactory.getLogger(OnenetDev24ServiceImpl.class);


    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device24");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev24> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev24> devs = onenetDev24Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                unit.setLv(devs.get(0).getLv());
                unit.setCOD(devs.get(0).getCOD());
                unit.setDO(devs.get(0).getDO());
                unit.setEC(devs.get(0).getEC());
                unit.setLvTemp(devs.get(0).getLvTemp());
                unit.setNHN(devs.get(0).getNHN());
                unit.setPH(devs.get(0).getPH());
                unit.setZD(devs.get(0).getZD());
                return unit;
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return unit;
    }


    @Override
    public OnenetDev24 selectWaterInfo() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(30);
            QueryWrapper<OnenetDev24> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev24> devs = onenetDev24Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                return devs.get(0);
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return null;
    }



    @Override
    public List<WaterUnit> selectWaterDetail(String waterType) {

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowEnd = now.withHour(now.getHour()).withMinute((now.getMinute() / 5) * 5).withSecond(0).withNano(0);
            LocalDateTime nowStart = nowEnd.minusHours(48);

            QueryWrapper<OnenetDev24> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",nowStart,now);
            queryWrapper.orderByAsc("update_time");

            List<OnenetDev24> dev24s = onenetDev24Mapper.selectList(queryWrapper);
            if (dev24s == null || dev24s.size() == 0){
                return null;
            }

            List<WaterUnit> waterUnits = new ArrayList<>();

            for (int i = 0; i <= 576; i++) {
                LocalDateTime start = nowStart.plusMinutes(5 * i);
                LocalDateTime end = start.plusMinutes(5);
                WaterUnit unit = new WaterUnit();
                unit.setUpdateTime(DateLocalUtils.parseTimeToStr(start));

                List<OnenetDev24> tempList = dev24s.stream()
                        .filter(onenetDev24 -> onenetDev24.getUpdateTime().after(DateLocalUtils.parseTimeToDate(start)))
                        .filter(onenetDev24 -> onenetDev24.getUpdateTime().before(DateLocalUtils.parseTimeToDate(end)))
                        .filter(onenetDev24 -> onenetDev24.getFieldValue(waterType) != null)
                        .collect(Collectors.toList());

                if (tempList.size() == 0){
                    unit.setValue(null);
                }else {
                    unit.setValue(tempList.get(0).getFieldValue(waterType));
                }


                waterUnits.add(unit);
            }
            

            return waterUnits;
        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


}
