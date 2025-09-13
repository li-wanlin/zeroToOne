package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.UNVInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UNVInfoService extends IService<UNVInfo> {


    Boolean insertByInfo(String inputTime ,MultipartFile[] files);


    List<String> getFileNames(String inputTime);


    byte[] previewByName(String fileName);


    List<String> getDateStrs(String inputTime);
}
