package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.GNSSData;
import com.jnl.entity.SafetyWarn;
import com.jnl.entity.SeepageInfo;
import com.jnl.vo.fourPreventVo.WarnResponse;

public interface SafetyWarnService extends IService<SafetyWarn> {

    void saveGNSS(GNSSData gnss);

    void saveOnenet(SeepageInfo info);


    WarnResponse selectLatestOver();


    WarnResponse pagedQueryOver(Integer pageNum,Integer pageSize,String year);


}
