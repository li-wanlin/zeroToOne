package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.OverLimit;
import com.stg.vo.fourPreventVo.OverResponse;

import java.util.List;

public interface OverLimitService extends IService<OverLimit> {




    OverResponse selectLatestOver();


    OverResponse pagedQueryOver(Integer pageNum,Integer pageSize,String year);


}
