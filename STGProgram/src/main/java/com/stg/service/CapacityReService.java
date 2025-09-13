package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.CapacityRe;
import com.stg.vo.capacityVo.CapaResponse;
import com.stg.vo.capacityVo.CapaUnit;
import org.springframework.web.multipart.MultipartFile;

public interface CapacityReService extends IService<CapacityRe> {


    Boolean insertByInfo(CapaUnit unit, MultipartFile file);

    Boolean deleteByInfo(CapaUnit unit);

    Boolean updateByInfo(CapaUnit unit, MultipartFile file);

    CapaResponse pagedQuery(Integer pageNum, Integer pageSize, String year);

    byte[] previewById(Integer id, long startByte);


}
