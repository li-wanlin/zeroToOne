package com.jnl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.EduTrain;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EduTrainService extends IService<EduTrain> {

    List<EduTrain> selectLatestFifty();


    Boolean InsertByEdu(EduTrain edu, MultipartFile[] files);


    Boolean DeleteByEdu(EduTrain edu);


    Boolean UpdateByEdu(EduTrain edu,MultipartFile[] files);


    IPage<EduTrain> pagedQuery(Integer pageNum,Integer pageSize);


    IPage<EduTrain> pagedQueryByYear(Integer pageNum,Integer pageSize,String year);



    List<String> getFileNames(Integer imgId);


    byte[] previewByName(String fileName);



}
