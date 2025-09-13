package com.stg.controller;


import com.stg.service.impl.FloodServiceImpl;
import com.stg.vo.floodVo.FloodResponse;
import com.stg.vo.floodVo.FloodUnit;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.patrolVo.FpImgResponse;
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
public class FloodController {

    @Resource
    FloodServiceImpl floodService;

    private static final Logger logger = LoggerFactory.getLogger(AnnualController.class);


    @GetMapping("/fourManagement/Flood/selectLatestTen")
    public FloodResponse selectLatestTen(){
        FloodResponse response = new FloodResponse();
        Meta meta = new Meta();

        response.setMeta(meta);
        try {
            List<FloodUnit> units = floodService.selectLatestTen();
            if (units != null){
                meta.setStatus(200);
                meta.setMsg("获取防汛保障分页数据成功");
                response.setFloodUnits(units);
                return response;
            }
        }catch (Exception e){
            logger.error("获取防汛保障分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取防汛保障分页数据失败");
        return response;
    }




    @PostMapping("/fourManagement/Flood/insertByInfo")
    public Meta insertByInfo(FloodUnit unit,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        try {
            Boolean insert = floodService.insertByInfo(unit,files);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入防汛保障数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("插入防汛保障数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("插入防汛保障数据失败");
        return meta;
    }



    @DeleteMapping("/fourManagement/Flood/deleteByInfo")
    public Meta deleteByInfo(@RequestBody FloodUnit unit){
        Meta meta = new Meta();
        try {
            Boolean delete = floodService.deleteByInfo(unit);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除防汛保障数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除防汛保障数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除防汛保障数据失败");
        return meta;
    }


    @PutMapping("/fourManagement/Flood/updateByInfo")
    public Meta updateByInfo(FloodUnit unit,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        try {
            Boolean update = floodService.updateByInfo(unit,files);
            if (update){
                meta.setStatus(200);
                meta.setMsg("更新防汛保障数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("更新防汛保障数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新防汛保障数据失败");
        return meta;
    }


    @GetMapping("/fourManagement/Flood/pagedQuery")
    public FloodResponse pagedQueryByYear(@RequestParam(value = "pageNum") Integer pageNum,
                                          @RequestParam(value = "pageSize") Integer pageSize,
                                          @RequestParam(value = "year",required = false) String year){
        FloodResponse response = new FloodResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            FloodResponse floodResponse = floodService.pagedQuery(pageNum, pageSize,year);
            if (floodResponse != null){
                return floodResponse;
            }
        }catch (Exception e){
            logger.error("获取防汛保障分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取防汛保障分页数据失败");
        return response;

    }



    @PostMapping("/fourManagement/Flood/getFileNames")
    public FpImgResponse getFileNames(@RequestBody FloodUnit unit){

        FpImgResponse response = new FpImgResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<String> fileNames = floodService.getFileNames(unit.getId());
            if (fileNames != null && fileNames.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取防汛保障图片数据成功");
                response.setFileNames(fileNames);
                return response;
            }else if (fileNames != null){
                meta.setStatus(200);
                meta.setMsg("该行没有图片");
                return response;
            }

        }catch (Exception e){
            logger.error("获取防汛保障图片数据发生异常",e);
        }
        meta.setStatus(100);
        meta.setMsg("获取防汛保障图片数据失败");
        return response;
    }





    @GetMapping("/fourManagement/Flood/previewByName")
    public ResponseEntity<org.springframework.core.io.Resource> previewByName(@RequestParam(value = "fileName") String fileName){
        try {

            byte[] fileBytes = floodService.previewByName(fileName);

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
            logger.error("在controller中服务器中下载文件时发生异常",e);
        }
        return ResponseEntity.notFound().build();
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
