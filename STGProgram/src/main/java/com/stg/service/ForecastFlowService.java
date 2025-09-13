package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.ForecastFlow;

public interface ForecastFlowService extends IService<ForecastFlow> {

    ForecastFlow selectForecastFlow();


}
