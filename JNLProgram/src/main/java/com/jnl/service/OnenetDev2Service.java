package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev2;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.RainUnit;

import java.util.List;

public interface OnenetDev2Service extends IService<OnenetDev2> {

    void parseDev2MsgList(List<OnenetParams> pendingDev2List);


    RainUnit selectRealTimeRain();


    DevUnit selectLatest();

}
