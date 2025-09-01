package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.AverageFlow;
import com.jnl.vo.matlabVo.ResUnit;

public interface AverageFlowService extends IService<AverageFlow> {

    AverageFlow selectAverageFlow();

}
