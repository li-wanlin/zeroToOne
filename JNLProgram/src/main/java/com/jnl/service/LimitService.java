package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.PerDelimit;
import com.jnl.vo.perDelimitVo.LimitResponse;
import com.jnl.vo.perDelimitVo.LimitUnit;
import org.springframework.web.multipart.MultipartFile;

public interface LimitService extends IService<PerDelimit> {


    Boolean insertByInfo(LimitUnit unit, MultipartFile file);

    Boolean deleteByInfo(LimitUnit unit);

    Boolean updateByInfo(LimitUnit unit, MultipartFile file);

    LimitResponse pagedQuery(Integer pageNum, Integer pageSize, String year);

    byte[] previewById(Integer id, long startByte);

}
