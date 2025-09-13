package com.stg.task;


import com.stg.mapper.WeatherMapper;
import com.stg.service.impl.WeatherRealServiceImpl;
import com.stg.service.impl.WeatherServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Component
public class WeatherTask {


    @Resource
    WeatherMapper weatherMapper;

    @Resource
    WeatherServiceImpl weatherService;

    @Resource
    WeatherRealServiceImpl weatherRealService;


    private static final Logger logger = LoggerFactory.getLogger(WeatherTask.class);


    /**
     * 获取72小时内降水和温度，每小时更新一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void Weather(){
        weatherService.weatherCall();
    }




    /**
     * 获取天气预报实时数据
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Async("asyncExecutor")
    public void WeatherRealCal(){
        weatherRealService.WeatherRealCall();
    }



}
