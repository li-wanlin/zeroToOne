package com.stg.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stg.entity.*;
import com.stg.mapper.*;
import com.stg.service.impl.StageDayServiceImpl;
import com.stg.service.impl.StageHourServiceImpl;
import com.stg.utils.DateLocalUtils;
import com.stg.mapper.StageHourMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//@Component
public class StageTask {


    @Resource
    OnenetDev22Mapper onenetDev22Mapper;

    @Resource
    OnenetDev23Mapper onenetDev23Mapper;

    @Resource
    OnenetDev24Mapper onenetDev24Mapper;



    @Resource
    StageHourMapper stageHourMapper;

    @Resource
    StageHourServiceImpl stageHourService;


    @Resource
    StageDayMapper stageDayMapper;

    @Resource
    StageDayServiceImpl stageDayService;


    private static final Logger logger = LoggerFactory.getLogger(StageTask.class);

    private static final DecimalFormat decimalFormat = new DecimalFormat("###0.00");// 格式化设置



    @Scheduled(cron = "0 0 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranStageHour(){
        try {

            LocalDate nowDate = LocalDate.now();
            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime start = now.minusHours(1);

            Double lv = null;
            Double lv2 = null;
            Double lv3 = null;


            QueryWrapper<OnenetDev22> dev22Wrapper = new QueryWrapper<>();
            dev22Wrapper.between("update_time",start,now);
            dev22Wrapper.le("lv",944);
            dev22Wrapper.orderByDesc("update_time");


            List<OnenetDev22> onenetDev22s = onenetDev22Mapper.selectList(dev22Wrapper);
            if (onenetDev22s != null && onenetDev22s.size() > 0){
                lv = onenetDev22s.get(0).getLv() == null ? null :BigDecimal.valueOf(onenetDev22s.get(0).getLv())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
            }



            QueryWrapper<OnenetDev23> dev23Wrapper = new QueryWrapper<>();
            dev23Wrapper.between("update_time",start,now);
            dev23Wrapper.orderByDesc("update_time");

            List<OnenetDev23> onenetDev23s = onenetDev23Mapper.selectList(dev23Wrapper);
            if (onenetDev23s != null && onenetDev23s.size() > 0){
                lv2 = onenetDev23s.get(0).getLv() == null ? null :BigDecimal.valueOf(onenetDev23s.get(0).getLv())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
            }


            QueryWrapper<OnenetDev24> dev24Wrapper = new QueryWrapper<>();
            dev24Wrapper.between("update_time",start,now);
            dev24Wrapper.orderByDesc("update_time");

            List<OnenetDev24> onenetDev24s = onenetDev24Mapper.selectList(dev24Wrapper);
            if (onenetDev24s != null && onenetDev24s.size() > 0){
                lv3 = onenetDev24s.get(0).getLv() == null ? null :BigDecimal.valueOf(onenetDev24s.get(0).getLv())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
            }


            StageHour stageHour = new StageHour();
            stageHour.setUpdateTime(DateLocalUtils.parseTimeToDate(now));
            stageHour.setLv(lv);
            stageHour.setLv2(lv2);
            stageHour.setLv3(lv3);

            stageHourService.saveOrUpdate(stageHour,new LambdaUpdateWrapper<StageHour>()
                    .eq(StageHour::getUpdateTime, stageHour.getUpdateTime()));


            //stageHourService.saveOrUpdate(stageHour);

            logger.info("水位小时级数据更新成功");


            StageDay stageDay = new StageDay();
            stageDay.setUpdateTime(DateLocalUtils.parseLocalDateToDateStart(nowDate));
            stageDay.setLv(lv);
            stageDay.setLv2(lv2);
            stageDay.setLv3(lv3);

            stageDayService.saveOrUpdate(stageDay,new LambdaUpdateWrapper<StageDay>().eq(StageDay::getUpdateTime,stageDay.getUpdateTime()));

            logger.info("水位天级数据更新成功");

        }catch (Exception e){
            logger.error("水位报表更新发生异常",e);
        }
    }










}
