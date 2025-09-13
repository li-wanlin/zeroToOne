package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.WeatherForecast;
import com.stg.vo.fourPreventVo.RainRangeUnit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WeatherService extends IService<WeatherForecast> {

    WeatherForecast selectLatest();

    @Transactional
    void weatherCall();


    List<RainRangeUnit> forecastThree();



}
