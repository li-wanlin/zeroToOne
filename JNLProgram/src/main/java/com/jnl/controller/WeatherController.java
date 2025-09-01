package com.jnl.controller;


import com.jnl.entity.WeatherReal;
import com.jnl.service.impl.WeatherRealServiceImpl;
import com.jnl.service.impl.WeatherServiceImpl;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.WeatherVo.WeatherResponse;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class WeatherController {


    @Resource
    WeatherServiceImpl weatherService;

    @Resource
    WeatherRealServiceImpl weatherRealService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);



    @GetMapping("/homePage/Weather/selectLatest")
    public WeatherResponse selectLatest(){
        WeatherResponse response = new WeatherResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            WeatherReal real = weatherRealService.selectRealTime();
            if (real != null){
                response.setDateTime(DateLocalUtils.parseDateToStr(real.getDateTime()));
                response.setSkycon(real.getSkycon());
                response.setTemperature(real.getTemperature());
                meta.setStatus(200);
                meta.setMsg("获取天气数据成功");
                return response;
            }

        }catch (Exception e){
            logger.error("获取天气数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取天气数据失败");
        return response;
    }




}
