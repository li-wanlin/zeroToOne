package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.RealTimeFlow;

public interface RealTimeFlowService extends IService<RealTimeFlow> {

    RealTimeFlow averageCal();


}
