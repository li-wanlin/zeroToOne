package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.ProjectPatrol;
import com.jnl.vo.patrolVo.ProPatrolResponse;
import com.jnl.vo.patrolVo.ProPatrolUnit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectPatrolService extends IService<ProjectPatrol> {

    List<ProPatrolUnit> selectLatestFifty();


    Boolean insertByInfo(ProPatrolUnit proPatrolUnit, MultipartFile[] files);

    Boolean deleteByInfo(ProPatrolUnit proPatrolUnit);


    Boolean updateByInfo(ProPatrolUnit proPatrolUnit,MultipartFile[] files);


    ProPatrolResponse pagedQuery(Integer pageNum, Integer pageSize);


    ProPatrolResponse pagedQueryByYear(Integer pageNum, Integer pageSize,String year);


    List<String> getFileNames(Integer imgId);


    byte[] previewByName(String fileName);


}
