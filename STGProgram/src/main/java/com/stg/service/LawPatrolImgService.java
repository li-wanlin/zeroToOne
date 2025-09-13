package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.LawPatrolImg;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LawPatrolImgService extends IService<LawPatrolImg> {

    Boolean insertByInfo(Integer lawId, MultipartFile[] files);


    List<String> getFileNames(Integer lawId);


    void deleteByLawId(Integer lawId);


    byte[] previewByName(String fileName);


}
