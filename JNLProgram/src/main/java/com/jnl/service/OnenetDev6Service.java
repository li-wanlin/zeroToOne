package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev6;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;

import java.util.List;

public interface OnenetDev6Service extends IService<OnenetDev6> {

    void parseDev6MsgList(List<OnenetParams> pendingDev6List);


    DevUnit selectLatest();


}
