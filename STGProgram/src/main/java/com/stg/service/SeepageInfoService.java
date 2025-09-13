package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.SeepageInfo;
import com.stg.vo.seepageVo.SeepageUnit;

import java.util.List;

public interface SeepageInfoService extends IService<SeepageInfo> {

    List<SeepageUnit> getSixHour(String type);


}
