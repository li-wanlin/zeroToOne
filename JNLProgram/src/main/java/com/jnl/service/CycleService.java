package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.DeviceCycle;
import com.jnl.vo.cycleVo.CycleResponse;
import com.jnl.vo.cycleVo.CycleUnit;

public interface CycleService extends IService<DeviceCycle> {


    Boolean insertByInfo(CycleUnit unit);

    Boolean deleteByInfo(CycleUnit unit);

    Boolean updateByInfo(CycleUnit unit);

    CycleResponse pagedQuery(Integer pageNum,Integer pageSize,String year);

}
