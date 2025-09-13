package com.stg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stg.entity.DisplayImg;
import com.stg.vo.display.DisplayImgResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DisplayImgService extends IService<DisplayImg> {


    Boolean insertByInfo(Integer catalogNumber, MultipartFile[] files);


    DisplayImgResponse selectDisplay(Integer catalogNumber);



}
