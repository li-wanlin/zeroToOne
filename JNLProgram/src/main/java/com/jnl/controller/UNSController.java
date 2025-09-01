package com.jnl.controller;


import com.jnl.service.impl.UNSInfoServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import com.jnl.vo.imageInfoVo.UNSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.List;

@RestController
public class UNSController {

    @Resource
    UNSInfoServiceImpl unsInfoService;

    private static final Logger logger = LoggerFactory.getLogger(UNSController.class);




    @PostMapping("/fourTotal/UNS/insertByInfo")
    public Meta insertByInfo(@RequestParam(value = "inputTime") String inputTime,
                             @RequestParam(value = "files") MultipartFile[] files){

        Meta meta = new Meta();
        try {
            Boolean insert = unsInfoService.insertByInfo(inputTime, files);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入无人船巡查数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入无人船巡查数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("插入无人船巡查数据失败");
        return meta;
    }



    @PostMapping("/fourTotal/UNS/getFileNames")
    public UNSResponse getFileNames(@RequestBody ImageGeneralInfo info){

        UNSResponse response = new UNSResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<String> fileNames = unsInfoService.getFileNames(info.getInputTime());
            if (fileNames != null && fileNames.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取无人船巡查数据成功");
                response.setInputTime(info.getInputTime());
                response.setFileNames(fileNames);
                return response;
            }

        }catch (Exception e){
            logger.error("获取无人船巡查数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取无人船巡查数据失败");
        return response;
    }


    @GetMapping("/fourTotal/UNS/previewByName")
    public ResponseEntity<org.springframework.core.io.Resource> previewByName(@RequestParam(value = "fileName") String fileName){
        try {

            byte[] fileBytes = unsInfoService.previewByName(fileName);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(fileName, "UTF-8");


            //设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,"inline; filename*=UTF-8'' " + encodeFilename);//inline,attachment


            headers.setContentType(this.getContentType(fileName));
            headers.setContentLength(fileBytes.length);


            //30天缓存时间
            Integer cache = 60 * 60 * 24 * 30;
            headers.setCacheControl("public, max-age=" + cache.toString());


            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }catch (Exception e){
            logger.error("在controller中服务器中下载PDF文件时发生异常",e);
        }
        return ResponseEntity.notFound().build();
    }




    @GetMapping("/fourTotal/UNS/getDateStrs")
    public UNSResponse getDateStrs(@RequestParam(value = "inputTime") String inputTime){

        UNSResponse response = new UNSResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<String> dateStrs = unsInfoService.getDateStrs(inputTime);
            if (dateStrs != null && dateStrs.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取无人船巡查日期列表数据成功");
                response.setInputTime(inputTime);
                response.setDateStrs(dateStrs);
                return response;
            }

        }catch (Exception e){
            logger.error("获取无人船巡查日期列表数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取无人船巡查日期列表数据失败");
        return response;
    }



    //根据文件扩展名获取Content-Type
    private MediaType getContentType(String fileName){
        try{
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch(fileExtension){
                case "jpg":
                case "jpeg":
                    return MediaType.IMAGE_JPEG;
                case "png":
                    return MediaType.IMAGE_PNG;
                case "gif":
                    return MediaType.IMAGE_GIF;
                default:
                    return MediaType.APPLICATION_OCTET_STREAM;
            }
        }catch (Exception e){
            logger.error("获取图片扩展名时发生错误",e);
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }




}
