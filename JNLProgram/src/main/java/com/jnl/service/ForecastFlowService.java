package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.ForecastFlow;

public interface ForecastFlowService extends IService<ForecastFlow> {

    ForecastFlow selectForecastFlow();


}
