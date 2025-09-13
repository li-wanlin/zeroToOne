package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.FloodForecast;

public interface FloodForecastService extends IService<FloodForecast> {

    FloodForecast selectFloodForecast();

}
