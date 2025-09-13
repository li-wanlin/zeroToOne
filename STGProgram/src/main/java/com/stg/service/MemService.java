package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.ProMemoirs;
import com.stg.vo.memoirsVo.MemUnit;

import java.util.List;

public interface MemService extends IService<ProMemoirs> {


    Boolean insertByInfo(MemUnit unit);

    Boolean deleteByInfo(MemUnit unit);

    Boolean updateByInfo(MemUnit unit);

    List<MemUnit> selectAll();

}
