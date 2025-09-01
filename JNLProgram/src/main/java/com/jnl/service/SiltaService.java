package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.SiltationControl;
import com.jnl.vo.siltaVo.SiltaResponse;
import com.jnl.vo.siltaVo.SiltaUnit;
import org.springframework.web.multipart.MultipartFile;

public interface SiltaService extends IService<SiltationControl> {

    Boolean insertByInfo(SiltaUnit unit, MultipartFile file);

    Boolean deleteByInfo(SiltaUnit unit);

    Boolean updateByInfo(SiltaUnit unit, MultipartFile file);

    SiltaResponse pagedQuery(Integer pageNum, Integer pageSize);

    byte[] previewById(Integer id, long startByte);

}
