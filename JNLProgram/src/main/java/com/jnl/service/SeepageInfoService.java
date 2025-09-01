package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.SeepageInfo;
import com.jnl.vo.seepageVo.SeepageUnit;

import java.util.List;

public interface SeepageInfoService extends IService<SeepageInfo> {

    List<SeepageUnit> getSixHour(String type);



    List<SeepageUnit> getLatest();





}
