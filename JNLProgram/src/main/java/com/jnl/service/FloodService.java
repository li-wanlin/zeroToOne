package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.FloodPrevent;
import com.jnl.vo.floodVo.FloodResponse;
import com.jnl.vo.floodVo.FloodUnit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FloodService extends IService<FloodPrevent> {


    List<FloodUnit> selectLatestTen();


    Boolean insertByInfo(FloodUnit unit, MultipartFile[] files);


    Boolean deleteByInfo(FloodUnit unit);


    Boolean updateByInfo(FloodUnit unit,MultipartFile[] files);


    FloodResponse pagedQuery(Integer pageNum, Integer pageSize, String year);


    List<String> getFileNames(Integer imgId);


    byte[] previewByName(String fileName);




}
