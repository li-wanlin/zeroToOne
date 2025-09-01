package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.RealTimeFlow;

public interface RealTimeFlowService extends IService<RealTimeFlow> {

    RealTimeFlow averageCal();


    RealTimeFlow selectRealTimeFlow();


}
