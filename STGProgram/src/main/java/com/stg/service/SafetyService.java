package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.Safety;

import java.util.List;

public interface SafetyService extends IService<Safety> {


    List<Safety> selectAllSort();


    Boolean updateByList(List<Safety> safeties);


}
