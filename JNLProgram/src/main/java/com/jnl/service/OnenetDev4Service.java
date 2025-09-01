package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev4;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.RainUnit;

import java.util.List;

public interface OnenetDev4Service extends IService<OnenetDev4> {

    void parseDev4MsgList(List<OnenetParams> pendingDev4List);


    RainUnit selectRealTimeRain();


    DevUnit selectLatest();




}
