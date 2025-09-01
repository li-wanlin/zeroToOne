package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.AnnualRepair;
import com.jnl.vo.annualVo.AnnualResponse;
import com.jnl.vo.annualVo.AnnualUnit;

public interface AnnualService extends IService<AnnualRepair> {

    Boolean insertByInfo(AnnualUnit unit);


    Boolean deleteByInfo(AnnualUnit unit);


    Boolean updateByInfo(AnnualUnit unit);


    AnnualResponse pagedQuery(Integer pageNum,Integer pageSize,String year);


}
