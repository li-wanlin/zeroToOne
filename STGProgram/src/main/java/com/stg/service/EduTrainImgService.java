package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.EduTrainImg;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EduTrainImgService extends IService<EduTrainImg> {

    Boolean insertByInfo(Integer imgId, MultipartFile[] files);


    List<String> getFileNames(Integer imgId);


    void deleteByImgId(Integer imgId);


    byte[] previewByName(String fileName);




}
