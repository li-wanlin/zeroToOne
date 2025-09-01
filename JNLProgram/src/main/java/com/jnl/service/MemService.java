package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.ProMemoirs;
import com.jnl.vo.memoirsVo.MemUnit;

import java.util.List;

public interface MemService extends IService<ProMemoirs> {


    Boolean insertByInfo(MemUnit unit);

    Boolean deleteByInfo(MemUnit unit);

    Boolean updateByInfo(MemUnit unit);

    List<MemUnit> selectAll();

}
