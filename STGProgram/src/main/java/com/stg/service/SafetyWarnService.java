package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.GNSSData;
import com.stg.entity.SafetyWarn;
import com.stg.entity.SeepageInfo;
import com.stg.vo.fourPreventVo.WarnResponse;

public interface SafetyWarnService extends IService<SafetyWarn> {

    void saveGNSS(GNSSData gnss);

    void saveOnenet(SeepageInfo info);


    WarnResponse selectLatestOver();


    WarnResponse pagedQueryOver(Integer pageNum,Integer pageSize,String year);


}
