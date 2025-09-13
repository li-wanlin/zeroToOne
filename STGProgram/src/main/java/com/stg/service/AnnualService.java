package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.AnnualRepair;
import com.stg.vo.annualVo.AnnualResponse;
import com.stg.vo.annualVo.AnnualUnit;

public interface AnnualService extends IService<AnnualRepair> {

    Boolean insertByInfo(AnnualUnit unit);


    Boolean deleteByInfo(AnnualUnit unit);


    Boolean updateByInfo(AnnualUnit unit);


    AnnualResponse pagedQuery(Integer pageNum,Integer pageSize,String year);


}
