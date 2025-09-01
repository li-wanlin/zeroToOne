package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.FileStore;
import com.jnl.vo.fileVo.GeneralInfo;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import org.springframework.web.multipart.MultipartFile;

public interface FileStoreService extends IService<FileStore> {


    //1.给定文件、路径、关联ID、关联类型，按照指定路径存入文件，返回文件名、完整路径（指定路径+文件名）
    //2.给定关联ID、关联类型、文件名，删除单个文件
    //3.给定关联ID，关联类型，批量删除文件
    //4.给定关联ID、关联类型、文件名，返回二进制流






    GeneralInfo generalUpload(MultipartFile[] files, String path, Integer relateId, String relateType);



    GeneralInfo singleDeleteFile(Integer relateId, String relateType, String fileName);



    GeneralInfo batchDeleteFile(Integer relateId, String relateType);




    byte[] generalPreview(Integer relateId, String relateType, String fileName);








}
