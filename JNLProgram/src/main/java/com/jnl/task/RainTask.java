package com.jnl.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnl.entity.RainDay;
import com.jnl.entity.RainFallData;
import com.jnl.entity.RealTimeData;
import com.jnl.mapper.*;
import com.jnl.service.impl.*;
import com.jnl.utils.DateLocalUtils;
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
import java.util.OptionalDouble;

//@Component
public class RainTask {




    @Resource
    RainFallDataServiceImpl rainFallDataService;

    @Resource
    RainFallDataMapper rainFallDataMapper;



    @Resource
    RainDayServiceImpl rainDayService;






    private static final Logger logger = LoggerFactory.getLogger(RainTask.class);


    /**
     * 定时任务，每1小时遍历5个雨量设备表（OnenetDev1~OnenetDev5），将近3天内数据进行计算，计算完成后将数据插入小时表RainFallData，插入时已有数据进行更新
     */
    @Scheduled(cron = "0 0 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void RainFall(){
        rainFallDataService.rainFallCall();
    }



    @Scheduled(cron = "0/10 * * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranRainDay(){
        try {

            LocalDate nowDate = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();

            int hour = now.getHour();
            if (hour == 0){
                nowDate = nowDate.minusDays(1);
            }


            Date start = DateLocalUtils.parseLocalDateToDateStart(nowDate);
            Date end = DateLocalUtils.parseLocalDateToDateEnd(nowDate);

            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",start,end);

            List<RainFallData> rainFallDatas = rainFallDataMapper.selectList(queryWrapper);

            if (rainFallDatas == null || rainFallDatas.size() == 0){
                return;
            }


            OptionalDouble rainfall1 = rainFallDatas.stream().filter(rainFallData -> rainFallData.getRainfall1() != null)
                    .mapToDouble(RainFallData::getRainfall1)
                    .reduce(Double::sum);


            OptionalDouble rainfall2 = rainFallDatas.stream().filter(rainFallData -> rainFallData.getRainfall2() != null)
                    .mapToDouble(RainFallData::getRainfall2)
                    .reduce(Double::sum);

            OptionalDouble rainfall3 = rainFallDatas.stream().filter(rainFallData -> rainFallData.getRainfall3() != null)
                    .mapToDouble(RainFallData::getRainfall3)
                    .reduce(Double::sum);

            OptionalDouble rainfall4 = rainFallDatas.stream().filter(rainFallData -> rainFallData.getRainfall4() != null)
                    .mapToDouble(RainFallData::getRainfall4)
                    .reduce(Double::sum);

            OptionalDouble rainfall5 = rainFallDatas.stream().filter(rainFallData -> rainFallData.getRainfall5() != null)
                    .mapToDouble(RainFallData::getRainfall5)
                    .reduce(Double::sum);

            RainDay rainDay = new RainDay();
            rainDay.setUpdateTime(start);
            rainDay.setRainfall1(rainfall1.isPresent() ? BigDecimal.valueOf(rainfall1.getAsDouble())
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue() : null);
            rainDay.setRainfall2(rainfall2.isPresent() ? BigDecimal.valueOf(rainfall2.getAsDouble())
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue() : null);
            rainDay.setRainfall3(rainfall3.isPresent() ? BigDecimal.valueOf(rainfall3.getAsDouble())
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue() : null);
            rainDay.setRainfall4(rainfall4.isPresent() ? BigDecimal.valueOf(rainfall4.getAsDouble())
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue() : null);
            rainDay.setRainfall5(rainfall5.isPresent() ? BigDecimal.valueOf(rainfall5.getAsDouble())
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue() : null);


            rainDayService.saveOrUpdate(rainDay,new LambdaUpdateWrapper<RainDay>()
                    .eq(RainDay::getUpdateTime,rainDay.getUpdateTime()));

        }catch (Exception e){
            logger.error("雨量天级报表更新发生异常",e);
        }
    }

















}
