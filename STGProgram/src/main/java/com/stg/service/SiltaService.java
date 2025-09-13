package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.SiltationControl;
import com.stg.vo.siltaVo.SiltaResponse;
import com.stg.vo.siltaVo.SiltaUnit;
import org.springframework.web.multipart.MultipartFile;

public interface SiltaService extends IService<SiltationControl> {

    Boolean insertByInfo(SiltaUnit unit, MultipartFile file);

    Boolean deleteByInfo(SiltaUnit unit);

    Boolean updateByInfo(SiltaUnit unit, MultipartFile file);

    SiltaResponse pagedQuery(Integer pageNum, Integer pageSize);

    byte[] previewById(Integer id, long startByte);

}
