package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev5;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.RainUnit;

import java.util.List;


public interface OnenetDev5Service extends IService<OnenetDev5> {

    Boolean parseMsg(List<OnenetDev5> onenetDev5List);

    void parseDev5MsgList(List<OnenetParams> pendingDev5List);


    RainUnit selectRealTimeRain();


    DevUnit selectLatest();



}
