package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.ImagesInfo;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagesInfoService extends IService<ImagesInfo> {


    ImagesInfo uploadImage(MultipartFile file);


    ImagesInfo selectImageByName(String imageName);


    ImagesInfo downloadImage(String imageName);



    ImageGeneralInfo generalUpload(MultipartFile file, String prefixPath);


    byte[] generalPreview(String filePath);



    void batchDeleteFiles(List<String> filePaths);



}
