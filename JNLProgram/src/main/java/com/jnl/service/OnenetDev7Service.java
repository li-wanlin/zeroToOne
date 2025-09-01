package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev7;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;

import java.util.List;

public interface OnenetDev7Service extends IService<OnenetDev7> {

    void parseDev7MsgList(List<OnenetParams> pendingDev7List);


    DevUnit selectLatest();

}
