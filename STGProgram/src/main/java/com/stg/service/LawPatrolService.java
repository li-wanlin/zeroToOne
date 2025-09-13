package com.stg.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.LawPatrol;
import com.stg.vo.patrolVo.PatrolInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LawPatrolService extends IService<LawPatrol> {

    List<PatrolInfo> selectLatestFifty();


    Boolean InsertByInfo(PatrolInfo patrolInfo, MultipartFile[] files);


    Boolean DeleteByInfo(PatrolInfo patrolInfo);


    Boolean UpdateByInfo(PatrolInfo patrolInfo,MultipartFile[] files);


    IPage<LawPatrol> pagedQuery(Integer pageNum, Integer pageSize);


    IPage<LawPatrol> pagedQueryByYear(Integer pageNum, Integer pageSize,String year);


    List<String> getFileNames(Integer lawId);


    byte[] previewByName(String fileName);


}
