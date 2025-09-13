package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.OnenetDev21;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;
import com.stg.vo.onenetVo.RainUnit;

import java.util.List;

public interface OnenetDev21Service extends IService<OnenetDev21> {

    /**
     * 获取实时雨量，取当前小时内，last-first;若为正值，则返回；若为负值，则返回last；
     * 若当前小时内只有一条数据，则first=上个小时最后一条数据，若上个小时内也没有数据，first=0；
     * 若当前小时内没有数据，返回0
     *
     * 改为
     * 获取实时雨量，取当前小时内，last-first;若为正值，则返回；若为负值，则返回last；
     * 若当前小时内只有一条数据，则first=12小时除当前小时外最后一条数据，若12小时内也没有数据，则返回null；
     * 若当前小时内没有数据，返回null
     * @return
     */
    RainUnit selectRealTimeRain();


    DevUnit selectLatest();


}
