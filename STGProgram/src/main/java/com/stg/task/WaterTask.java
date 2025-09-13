package com.stg.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stg.entity.OnenetDev24;
import com.stg.entity.WaterDay;
import com.stg.entity.WaterHour;
import com.stg.mapper.OnenetDev24Mapper;
import com.stg.mapper.WaterHourMapper;
import com.stg.service.impl.WaterDayServiceImpl;
import com.stg.service.impl.WaterHourServiceImpl;
import com.stg.utils.DateLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//@Component
public class WaterTask {

    @Resource
    OnenetDev24Mapper onenetDev24Mapper;

    @Resource
    WaterHourMapper waterHourMapper;

    @Resource
    WaterHourServiceImpl waterHourService;

    @Resource
    WaterDayServiceImpl waterDayService;


    private static final Logger logger = LoggerFactory.getLogger(WaterTask.class);



    @Scheduled(cron = "0 0 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranWaterHour(){
        try {
            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime start = now.minusHours(1);


            QueryWrapper<OnenetDev24> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");

            List<OnenetDev24> onenetDev24s = onenetDev24Mapper.selectList(queryWrapper);
            if (onenetDev24s == null || onenetDev24s.size() == 0){
                return;
            }
            OnenetDev24 onenetDev24 = onenetDev24s.get(0);

            WaterHour waterHour = new WaterHour();
            waterHour.setUpdateTime(DateLocalUtils.parseTimeToDate(now));
            waterHour.setPH(onenetDev24.getPH()==null?null: BigDecimal.valueOf(onenetDev24.getPH())
                    .setScale(2, RoundingMode.DOWN).doubleValue());
            waterHour.setEC(onenetDev24.getEC()==null?null: BigDecimal.valueOf(onenetDev24.getEC())
                    .setScale(2, RoundingMode.DOWN).doubleValue());
            waterHour.setDO(onenetDev24.getDO()==null?null: BigDecimal.valueOf(onenetDev24.getDO())
                    .setScale(2, RoundingMode.DOWN).doubleValue());
            waterHour.setCOD(onenetDev24.getCOD()==null?null: BigDecimal.valueOf(onenetDev24.getCOD())
                    .setScale(2, RoundingMode.DOWN).doubleValue());
            waterHour.setZD(onenetDev24.getZD()==null?null: BigDecimal.valueOf(onenetDev24.getZD())
                    .setScale(2, RoundingMode.DOWN).doubleValue());
            waterHour.setNHN(onenetDev24.getNHN()==null?null: BigDecimal.valueOf(onenetDev24.getNHN())
                    .setScale(2, RoundingMode.DOWN).doubleValue());
            waterHour.setLvTemp(onenetDev24.getLvTemp()==null?null: BigDecimal.valueOf(onenetDev24.getLvTemp())
                    .setScale(2, RoundingMode.DOWN).doubleValue());

            waterHourService.saveOrUpdate(waterHour,new LambdaUpdateWrapper<WaterHour>()
                    .eq(WaterHour::getUpdateTime,waterHour.getUpdateTime()));

            //logger.info("水质信息小时级数据更新成功");


        }catch (Exception e){
            logger.error("水质信息小时级数据更新发生异常",e);
        }
    }


    @Scheduled(cron = "0 5 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranWaterDay(){
        try {
            LocalDate now = LocalDate.now();

            Date start = DateLocalUtils.parseLocalDateToDateStart(now);
            Date end = DateLocalUtils.parseLocalDateToDateEnd(now);

            QueryWrapper<WaterHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            queryWrapper.orderByDesc("update_time");

            List<WaterHour> waterHours = waterHourMapper.selectList(queryWrapper);
            if (waterHours == null || waterHours.size() == 0){
                return;
            }

            WaterHour waterHour = waterHours.get(0);
            WaterDay waterDay = new WaterDay();
            waterDay.setUpdateTime(start);
            waterDay.setNHN(waterHour.getNHN());
            waterDay.setCOD(waterHour.getCOD());
            waterDay.setEC(waterHour.getEC());
            waterDay.setDO(waterHour.getDO());
            waterDay.setPH(waterHour.getPH());
            waterDay.setZD(waterHour.getZD());
            waterDay.setLvTemp(waterHour.getLvTemp());

            waterDayService.saveOrUpdate(waterDay,new LambdaUpdateWrapper<WaterDay>()
                    .eq(WaterDay::getUpdateTime,waterDay.getUpdateTime()));


            //logger.info("水质信息天级数据更新成功");


        }catch (Exception e){
            logger.error("水质信息天级数据更新发生异常",e);
        }
    }






}
