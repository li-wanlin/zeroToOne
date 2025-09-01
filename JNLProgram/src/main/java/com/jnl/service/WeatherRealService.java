package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.WeatherReal;

public interface WeatherRealService extends IService<WeatherReal> {

    WeatherReal selectRealTime();


    WeatherReal WeatherRealCall();


}
