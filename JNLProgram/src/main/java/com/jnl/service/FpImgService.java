package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.FpImg;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public interface FpImgService extends IService<FpImg> {


    Boolean insertByInfo(Integer imgId, MultipartFile[] files);


    List<String> getFileNames(Integer imgId);



    void deleteByImgId(Integer imgId);



    byte[] previewByName(String fileName);




}
