package com.jnl.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnl.entity.*;
import com.jnl.mapper.*;
import com.jnl.service.impl.StageDayServiceImpl;
import com.jnl.service.impl.StageHourServiceImpl;
import com.jnl.utils.DateLocalUtils;
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
    OnenetDev4Mapper onenetDev4Mapper;

    @Resource
    OnenetDev5Mapper onenetDev5Mapper;

    @Resource
    OnenetDev6Mapper onenetDev6Mapper;

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
            Double lv4 = null;
            Double lv5 = null;
            Double lv6 = null;


            //原两河口网关
            QueryWrapper<OnenetDev4> dev4Wrapper = new QueryWrapper<>();

            dev4Wrapper.between("update_time",start,now);
            dev4Wrapper.orderByDesc("update_time");

            dev4Wrapper.and(wrapper -> {
                wrapper.isNotNull("lv")
                        .or()
                        .isNotNull("rainFall");
            });

            List<OnenetDev4> onenetDev4s = onenetDev4Mapper.selectList(dev4Wrapper);





            if (onenetDev4s != null && onenetDev4s.size() > 0){
                lv = onenetDev4s.get(0).getLv() == null ? null :BigDecimal.valueOf(onenetDev4s.get(0).getLv())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
            }


            //新增的两河口网关
            QueryWrapper<OnenetDev4> dev402Wrapper = new QueryWrapper<>();
            dev402Wrapper.between("update_time",start,now);
            dev402Wrapper.orderByDesc("update_time");

            dev402Wrapper.and(wrapper -> {
                wrapper.isNotNull("lv2")
                        .or()
                        .isNotNull("lv3");
            });

            List<OnenetDev4> onenetDev402s = onenetDev4Mapper.selectList(dev402Wrapper);


            if (onenetDev402s != null && onenetDev402s.size() > 0){
                lv2 = onenetDev402s.get(0).getLv2() == null ? null :BigDecimal.valueOf(onenetDev402s.get(0).getLv2())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
                lv3 = onenetDev402s.get(0).getLv3() == null ? null :BigDecimal.valueOf(onenetDev402s.get(0).getLv3())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();

                //暂时没有水位计
/*                lv4 = onenetDev4s.get(0).getLv4() == null ? null :BigDecimal.valueOf(onenetDev4s.get(0).getLv4())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();*/
            }





            QueryWrapper<OnenetDev5> dev5Wrapper = new QueryWrapper<>();
            dev5Wrapper.between("update_time",start,now);
            dev5Wrapper.orderByDesc("update_time");

            List<OnenetDev5> onenetDev5s = onenetDev5Mapper.selectList(dev5Wrapper);
            if (onenetDev5s != null && onenetDev5s.size() > 0){
                lv5 = onenetDev5s.get(0).getLv() == null ? null :BigDecimal.valueOf(onenetDev5s.get(0).getLv())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
            }


            QueryWrapper<OnenetDev6> dev6Wrapper = new QueryWrapper<>();
            dev6Wrapper.between("update_time",start,now);
            dev6Wrapper.orderByDesc("update_time");

            List<OnenetDev6> onenetDev6s = onenetDev6Mapper.selectList(dev6Wrapper);
            if (onenetDev6s != null && onenetDev6s.size() > 0){
                lv6 = onenetDev6s.get(0).getLv() == null ? null :BigDecimal.valueOf(onenetDev6s.get(0).getLv())
                        .setScale(2, RoundingMode.DOWN)
                        .doubleValue();
            }


            StageHour stageHour = new StageHour();
            stageHour.setUpdateTime(DateLocalUtils.parseTimeToDate(now));
            stageHour.setLv(lv);
            stageHour.setLv2(lv2);
            stageHour.setLv3(lv3);
            stageHour.setLv4(lv4);
            stageHour.setLv5(lv5);
            stageHour.setLv6(lv6);

            stageHourService.saveOrUpdate(stageHour,new LambdaUpdateWrapper<StageHour>()
                    .eq(StageHour::getUpdateTime, stageHour.getUpdateTime()));


            //stageHourService.saveOrUpdate(stageHour);

            //logger.info("水位小时级数据更新成功");


            StageDay stageDay = new StageDay();
            stageDay.setUpdateTime(DateLocalUtils.parseLocalDateToDateStart(nowDate));
            stageDay.setLv(lv);
            stageDay.setLv2(lv2);
            stageDay.setLv3(lv3);
            stageDay.setLv4(lv4);
            stageDay.setLv5(lv5);
            stageDay.setLv6(lv6);

            stageDayService.saveOrUpdate(stageDay,new LambdaUpdateWrapper<StageDay>().eq(StageDay::getUpdateTime,stageDay.getUpdateTime()));

            //logger.info("水位天级数据更新成功");

        }catch (Exception e){
            logger.error("水位报表更新发生异常",e);
        }
    }










}
