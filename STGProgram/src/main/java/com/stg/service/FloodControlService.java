package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.FloodControl;

import java.util.Map;

public interface FloodControlService extends IService<FloodControl> {

    FloodControl selectFloodControl();


    Map<String,String> selectPlan();


    Map<String,String> selectPlanSim();


}
