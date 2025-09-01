package com.jnl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jnl.entity.EmerSup;
import com.jnl.vo.emerVo.EmerResponse;
import com.jnl.vo.emerVo.EmerUnit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmerSupService extends IService<EmerSup> {


    List<EmerUnit> selectLatestTen();


    Boolean insertByInfo(EmerUnit unit, MultipartFile file);

    Boolean deleteByInfo(EmerUnit unit);

    Boolean updateByInfo(EmerUnit unit, MultipartFile file);

    EmerResponse pagedQuery(Integer pageNum, Integer pageSize, String year);

    byte[] previewById(Integer id, long startByte);



}
