package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.ProtectManage;
import com.stg.vo.protectVo.ProtectResponse;
import com.stg.vo.protectVo.ProtectUnit;


public interface ProtectService extends IService<ProtectManage> {

    ProtectResponse displayInfo();


    Boolean insertByInfo(ProtectUnit unit);


    Boolean deleteByInfo(ProtectUnit unit);


    Boolean updateByInfo(ProtectUnit unit);


    ProtectResponse pagedQuery(Integer pageNum,Integer pageSize);


    ProtectResponse pagedQueryByYear(Integer pageNum, Integer pageSize,String year);



}
