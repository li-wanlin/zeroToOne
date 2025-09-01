package com.jnl.controller;


import com.jnl.service.impl.DisplayImgServiceImpl;
import com.jnl.vo.display.DisplayImgResponse;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
public class DisplayImgController {


    @Resource
    DisplayImgServiceImpl displayImgService;


    private static final Logger logger = LoggerFactory.getLogger(DisplayImgController.class);



    @PostMapping("/DisplayImg/img/insertByInfo")
    public Meta insertByInfo(Integer catalogNumber, @RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入显示图片数据失败");

/*        if (files == null){
            logger.info("这次未加入图片数据");
        }else {
            logger.info("这次加入图片数量为：{}",files.length);
        }*/


        try {
            Boolean insert = displayImgService.insertByInfo(catalogNumber, files);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入显示图片数据成功");
            }
        }catch (Exception e){
            logger.error("插入显示图片数据发生异常",e);
        }
        return meta;
    }


    @GetMapping("/DisplayImg/img/selectDisplay")
    public DisplayImgResponse selectDisplay(@RequestParam Integer catalogNumber){
        return displayImgService.selectDisplay(catalogNumber);
    }





}
