package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.OnenetDev23;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;

import java.util.List;

public interface OnenetDev23Service extends IService<OnenetDev23> {


    DevUnit selectLatest();

}
