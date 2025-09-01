package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.OnenetDev4;
import com.jnl.entity.OnenetDev5;
import com.jnl.entity.OnenetDev6;
import com.jnl.entity.OverLimit;
import com.jnl.vo.fourPreventVo.OverResponse;

import java.util.List;

public interface OverLimitService extends IService<OverLimit> {


    void saveDeviceFour(List<OnenetDev4> list);


    void saveDeviceFive(List<OnenetDev5> list);


    void saveDeviceSix(List<OnenetDev6> list);


    OverResponse selectLatestOver();


    OverResponse pagedQueryOver(Integer pageNum,Integer pageSize,String year);


}
