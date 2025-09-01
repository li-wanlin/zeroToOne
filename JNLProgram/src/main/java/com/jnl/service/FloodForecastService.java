package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.FloodForecast;

public interface FloodForecastService extends IService<FloodForecast> {

    FloodForecast selectFloodForecast();

}
