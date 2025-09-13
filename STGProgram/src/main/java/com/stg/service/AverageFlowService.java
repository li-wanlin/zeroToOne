package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.AverageFlow;
import com.stg.vo.matlabVo.ResUnit;

public interface AverageFlowService extends IService<AverageFlow> {

    ResUnit selectAverageFlow();

}
