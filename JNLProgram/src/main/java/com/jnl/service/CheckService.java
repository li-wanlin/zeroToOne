package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.PitfallCheck;
import com.jnl.vo.checkVo.CheckResponse;
import com.jnl.vo.checkVo.CheckUnit;
import org.springframework.web.multipart.MultipartFile;

public interface CheckService extends IService<PitfallCheck> {



    Boolean insertByInfo(CheckUnit unit, MultipartFile file);

    Boolean deleteByInfo(CheckUnit unit);

    Boolean updateByInfo(CheckUnit unit, MultipartFile file);

    CheckResponse pagedQuery(Integer pageNum, Integer pageSize, String year);

    byte[] previewById(Integer id, long startByte);


}
