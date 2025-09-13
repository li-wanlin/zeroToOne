package com.stg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.SafetyDetail;
import org.springframework.web.multipart.MultipartFile;

public interface SafetyDetailService extends IService<SafetyDetail> {

    Boolean insertByInfo(String project, String time, String orgUnit, String bearUnit, String conclusion, MultipartFile file);


    Boolean deleteByInfo(Integer id);


    Boolean updateByInfo(Integer id,String project, String time, String orgUnit, String bearUnit, String conclusion, MultipartFile file);


    IPage<SafetyDetail> pageQuery(Integer pageNum,Integer pageSize);


    byte[] previewById(Integer id,long startByte);



}
