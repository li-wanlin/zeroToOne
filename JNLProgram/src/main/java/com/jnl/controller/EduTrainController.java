package com.jnl.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jnl.entity.EduTrain;
import com.jnl.service.impl.EduTrainServiceImpl;
import com.jnl.vo.eduVo.EduResponse;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.patrolVo.EduImgResponse;
import com.jnl.vo.patrolVo.LawImgResponse;
import com.jnl.vo.patrolVo.PatrolInfo;
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
public class EduTrainController {

    @Resource
    EduTrainServiceImpl eduTrainService;


    private static final Logger logger = LoggerFactory.getLogger(EduTrainController.class);


    @GetMapping("/fourGovern/EduTrain/selectLatestFifty")
    public EduResponse selectLatestFifty(){
        EduResponse response = new EduResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            List<EduTrain> eduTrains = eduTrainService.selectLatestFifty();
            if (eduTrains != null && eduTrains.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取教育培训最新50条数据成功");

                response.setEduTrains(eduTrains);
                return response;
            }

        }catch (Exception e){
            logger.error("获取教育培训最新50条数据Controller层发生异常",e);
        }
        return response;
    }


    @PostMapping("/fourGovern/EduTrain/insertByEdu")
    public Meta insertByEdu(EduTrain edu,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
/*        logger.info("这是教育培训除文件以外的数据：{}", JSON.toJSONString(edu));
        if (files == null){
            logger.info("这次教育培训未加入图片数据");
        }else {
            logger.info("这次教育培训加入图片数量为：{}",files.length);
        }*/

        try {
            Boolean insert = eduTrainService.InsertByEdu(edu,files);
            if (insert){
                meta.setStatus(200);
                meta.setMsg("插入教育培训数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入教育培训数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("插入教育培训数据失败");
        return meta;
    }


    @DeleteMapping("/fourGovern/EduTrain/deleteByEdu")
    public Meta deleteByEdu(@RequestBody EduTrain edu){
        Meta meta = new Meta();
        try {
            Boolean delete = eduTrainService.DeleteByEdu(edu);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除教育培训数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("删除教育培训数据Controller层发生异常",e);
        }

        meta.setStatus(200);
        meta.setMsg("删除教育培训数据失败");
        return meta;
    }


    @PutMapping("/fourGovern/EduTrain/updateByEdu")
    public Meta updateByEdu(EduTrain edu,@RequestParam(value = "files",required = false) MultipartFile[] files){
        Meta meta = new Meta();
        try {
            Boolean update = eduTrainService.UpdateByEdu(edu,files);
            if (update){
                meta.setStatus(200);
                meta.setMsg("修改教育培训数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("修改教育培训Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("修改教育培训数据失败");
        return meta;
    }


    @GetMapping("/fourGovern/EduTrain/pagedQuery")
    public EduResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                  @RequestParam(value = "pageSize") Integer pageSize){
        EduResponse response = new EduResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            IPage<EduTrain> page = eduTrainService.pagedQuery(pageNum, pageSize);
            if (page != null){
                List<EduTrain> eduTrains = page.getRecords();

                if (eduTrains != null && eduTrains.size() > 0){
                    long pages = page.getPages();

                    int startIndex = (pageNum - 1) * pageSize + 1;
                    for (EduTrain edu:eduTrains) {
                        edu.setOrderNum(startIndex++);
                    }


                    meta.setStatus(200);
                    meta.setMsg("分页查询教育培训数据成功");

                    response.setTotalPage((int)pages);
                    response.setCurrentPage(pageNum);
                    response.setTotalCount((int)page.getTotal());
                    response.setEduTrains(eduTrains);
                    return response;
                }
            }
        }catch (Exception e){
            logger.error("分页查询教育培训Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("分页查询教育培训失败");
        return response;
    }


    @GetMapping("/fourGovern/EduTrain/pagedQueryByYear")
    public EduResponse pagedQueryByYear(@RequestParam(value = "pageNum") Integer pageNum,
                                        @RequestParam(value = "pageSize") Integer pageSize,
                                        @RequestParam(value = "year") String year){
        EduResponse response = new EduResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            IPage<EduTrain> page = eduTrainService.pagedQueryByYear(pageNum, pageSize, year);
            if (page != null){
                List<EduTrain> eduTrains = page.getRecords();

                if (eduTrains != null && eduTrains.size() > 0){
                    long pages = page.getPages();

                    int startIndex = (pageNum - 1) * pageSize + 1;
                    for (EduTrain edu:eduTrains) {
                        edu.setOrderNum(startIndex++);
                    }


                    meta.setStatus(200);
                    meta.setMsg("通过时间分页查询教育培训数据成功");

                    response.setTotalPage((int)pages);
                    response.setCurrentPage(pageNum);
                    response.setTotalCount((int)page.getTotal());
                    response.setEduTrains(eduTrains);
                    return response;
                }
            }

        }catch (Exception e){
            logger.error("通过时间分页查询教育培训Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("通过时间分页查询教育培训数据失败");
        return response;
    }


    @PostMapping("/fourGovern/EduTrain/getFileNames")
    public EduImgResponse getFileNames(@RequestBody EduTrain edu){

        EduImgResponse response = new EduImgResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<String> fileNames = eduTrainService.getFileNames(edu.getId());
            if (fileNames != null && fileNames.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取教育培训图片数据成功");
                response.setFileNames(fileNames);
                return response;
            }else if (fileNames != null){
                meta.setStatus(200);
                meta.setMsg("该行没有图片");
                return response;
            }

        }catch (Exception e){
            logger.error("获取教育培训图片数据发生异常",e);
        }
        meta.setStatus(100);
        meta.setMsg("获取教育培训图片数据失败");
        return response;
    }




    @GetMapping("/fourGovern/EduTrain/previewByName")
    public ResponseEntity<org.springframework.core.io.Resource> previewByName(@RequestParam(value = "fileName") String fileName){
        try {

            byte[] fileBytes = eduTrainService.previewByName(fileName);

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
