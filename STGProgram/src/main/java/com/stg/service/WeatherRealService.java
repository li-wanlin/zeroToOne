package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.WeatherReal;

public interface WeatherRealService extends IService<WeatherReal> {

    WeatherReal selectRealTime();


    WeatherReal WeatherRealCall();


}
