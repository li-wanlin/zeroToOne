package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.Safety;

import java.util.List;

public interface SafetyService extends IService<Safety> {


    List<Safety> selectAllSort();


    Boolean updateByList(List<Safety> safeties);


}
