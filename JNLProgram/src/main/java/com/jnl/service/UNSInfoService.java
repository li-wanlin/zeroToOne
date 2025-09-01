package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.UNSInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UNSInfoService extends IService<UNSInfo> {

    Boolean insertByInfo(String inputTime , MultipartFile[] files);


    List<String> getFileNames(String inputTime);


    byte[] previewByName(String fileName);


    List<String> getDateStrs(String inputTime);

}
