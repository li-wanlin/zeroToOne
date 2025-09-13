package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.OnenetDev24;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;
import com.stg.vo.onenetVo.WaterInfoResponse;
import com.stg.vo.onenetVo.WaterUnit;

import java.util.List;

public interface OnenetDev24Service extends IService<OnenetDev24> {


    DevUnit selectLatest();


    OnenetDev24 selectWaterInfo();


    List<WaterUnit> selectWaterDetail(String waterType);

}
