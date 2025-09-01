package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev3;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.RainUnit;

import java.util.List;

public interface OnenetDev3Service extends IService<OnenetDev3> {


    void parseDev3MsgList(List<OnenetParams> pendingDev3List);


    RainUnit selectRealTimeRain();


    DevUnit selectLatest();
}
