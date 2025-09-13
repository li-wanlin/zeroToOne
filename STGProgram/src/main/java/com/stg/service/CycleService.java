package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.DeviceCycle;
import com.stg.vo.cycleVo.CycleResponse;
import com.stg.vo.cycleVo.CycleUnit;

public interface CycleService extends IService<DeviceCycle> {


    Boolean insertByInfo(CycleUnit unit);

    Boolean deleteByInfo(CycleUnit unit);

    Boolean updateByInfo(CycleUnit unit);

    CycleResponse pagedQuery(Integer pageNum,Integer pageSize,String year);

}
