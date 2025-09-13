package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.FileStore;
import com.stg.vo.fileVo.FilePreResponse;
import com.stg.vo.fileVo.GeneralInfo;
import org.springframework.web.multipart.MultipartFile;

public interface FileStoreService extends IService<FileStore> {


    //1.给定文件、路径、关联ID、关联类型，按照指定路径存入文件，返回文件名、完整路径（指定路径+文件名）
    //2.给定文件、路径、关联ID、关联类型、文件名，按照指定路径存入文件，返回文件名、完整路径（指定路径+文件名）


    //2.给定关联ID、关联类型、文件名，删除单个文件
    //3.给定关联ID，关联类型，批量删除文件
    //4.给定关联ID、关联类型、文件名，返回二进制流
    //5.给定图片在数据库中id、文件名，删除单个文件
    //6.给定图片在数据库中id、文件名，返回二进制流
    //7.给定关联ID、关联类型，返回该关联ID和类型下所有文件信息（图片和PDF分开）






    GeneralInfo generalUpload(MultipartFile[] files, String path, Integer relateId, String relateType);



    GeneralInfo generalUploadParam(MultipartFile[] files, String path, Integer relateId, String relateType,String nameParam);




    GeneralInfo singleDeleteFile(Integer relateId, String relateType, String fileName);



    GeneralInfo batchDeleteFile(Integer relateId, String relateType);




    byte[] generalPreview(Integer relateId, String relateType, String fileName);



    GeneralInfo singleDeleteNew(Integer id, String fileName);


    byte[] generalPreviewNew(Integer id, String fileName);



    FilePreResponse getFileName(Integer relateId, String relateType);





}
