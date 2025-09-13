package com.stg.controller;

import com.stg.service.impl.ProjectPatrolServiceImpl;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.patrolVo.ProPatrolResponse;
import com.stg.vo.patrolVo.ProPatrolUnit;
import com.stg.vo.patrolVo.ProjectImgResponse;
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
public class ProPatrolController {

    @Resource
    ProjectPatrolServiceImpl projectPatrolService;


    private static final Logger logger = LoggerFactory.getLogger(ProPatrolController.class);



    @GetMapping("/fourManagement/ProPatrol/selectLatestFifty")
    public ProPatrolResponse selectLatestFifty(){
        ProPatrolResponse proPatrolResponse = new ProPatrolResponse();
        Meta meta = new Meta();
        proPatrolResponse.setMeta(meta);
        try{
            List<ProPatrolUnit> proPatrolUnits = projectPatrolService.selectLatestFifty();
            if (proPatrolUnits != null && proPatrolUnits.size() != 0){
                meta.setStatus(200);
                meta.setMsg("获取工程巡查最新50条数据成功");

                proPatrolResponse.setProPatrolUnits(proPatrolUnits);
                return proPatrolResponse;
            }

        }catch (Exception e){
            logger.error("获取工程巡查最新50条数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取工程巡查最新50条数据失败");

        return proPatrolResponse;
    }


    @PostMapping("/fourManagement/ProPatrol/insertByInfo")
    public Meta insertByInfo(ProPatrolUnit proPatrolUnit,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        try {
            Boolean insert = projectPatrolService.insertByInfo(proPatrolUnit,files);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入工程巡查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("插入工程巡查数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("插入工程巡查数据失败");

        return meta;
    }


    @DeleteMapping("/fourManagement/ProPatrol/deleteByInfo")
    public Meta deleteByInfo(@RequestBody ProPatrolUnit proPatrolUnit){
        Meta meta = new Meta();
        try {
            Boolean delete = projectPatrolService.deleteByInfo(proPatrolUnit);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除工程巡查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除工程巡查数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除工程巡查数据失败");
        return meta;
    }


    @PutMapping("/fourManagement/ProPatrol/updateByInfo")
    public Meta updateByInfo(ProPatrolUnit proPatrolUnit,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        try {
            Boolean update = projectPatrolService.updateByInfo(proPatrolUnit,files);
            if (update){
                meta.setStatus(200);
                meta.setMsg("更新工程巡查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("更新工程巡查数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("更新工程巡查数据失败");
        return meta;
    }



    @GetMapping("/fourManagement/ProPatrol/pagedQuery")
    public ProPatrolResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                        @RequestParam(value = "pageSize") Integer pageSize){

        ProPatrolResponse response = new ProPatrolResponse();
        Meta meta = new Meta();
        response.setMeta(meta);


        try {
            ProPatrolResponse proPatrolResponse = projectPatrolService.pagedQuery(pageNum, pageSize);
            if (proPatrolResponse != null){
                return proPatrolResponse;
            }
        }catch (Exception e){
            logger.error("获取工程巡查分页数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取工程巡查分页数据失败");
        return response;
    }


    @GetMapping("/fourManagement/ProPatrol/pagedQueryByYear")
    public ProPatrolResponse pagedQueryByYear(@RequestParam(value = "pageNum") Integer pageNum,
                                              @RequestParam(value = "pageSize") Integer pageSize,
                                              @RequestParam(value = "year") String year){
        ProPatrolResponse response = new ProPatrolResponse();
        Meta meta = new Meta();
        response.setMeta(meta);


        try {
            ProPatrolResponse proPatrolResponse = projectPatrolService.pagedQueryByYear(pageNum, pageSize,year);
            if (proPatrolResponse != null){
                return proPatrolResponse;
            }
        }catch (Exception e){
            logger.error("获取按年工程巡查分页数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取工程巡查分页数据失败");
        return response;
    }





    @PostMapping("/fourManagement/ProPatrol/getFileNames")
    public ProjectImgResponse getFileNames(@RequestBody ProPatrolUnit proPatrolUnit){

        ProjectImgResponse response = new ProjectImgResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<String> fileNames = projectPatrolService.getFileNames(proPatrolUnit.getId());
            if (fileNames != null && fileNames.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取工程巡查图片数据成功");
                response.setFileNames(fileNames);
                return response;
            }else if (fileNames != null){
                meta.setStatus(200);
                meta.setMsg("该行没有图片");
                return response;
            }

        }catch (Exception e){
            logger.error("获取工程巡查图片数据发生异常",e);
        }
        meta.setStatus(100);
        meta.setMsg("获取工程巡查图片数据失败");
        return response;
    }




    @GetMapping("/fourManagement/ProPatrol/previewByName")
    public ResponseEntity<org.springframework.core.io.Resource> previewByName(@RequestParam(value = "fileName") String fileName){
        try {

            byte[] fileBytes = projectPatrolService.previewByName(fileName);

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
