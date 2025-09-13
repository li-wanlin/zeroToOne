package com.stg.controller;


import com.stg.service.impl.CapacityReServiceImpl;
import com.stg.vo.capacityVo.CapaResponse;
import com.stg.vo.capacityVo.CapaUnit;
import com.stg.vo.functionVo.Meta;
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

@RestController
public class CapacityReController {

    @Resource
    CapacityReServiceImpl capacityReService;


    private static final Logger logger = LoggerFactory.getLogger(CapacityReController.class);



    @PostMapping("/fourManagement/Capa/insertByInfo")
    public Meta insertByInfo(CapaUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入库容复核数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = capacityReService.insertByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入库容复核数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入库容复核数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourManagement/Capa/deleteByInfo")
    public Meta deleteByInfo(@RequestBody CapaUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除库容复核数据失败");
        try {
            Boolean delete = capacityReService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除库容复核数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除库容复核数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourManagement/Capa/updateByInfo")
    public Meta updateByInfo(CapaUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新库容复核数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = capacityReService.updateByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新库容复核数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("库容复核更新数据Controller层发生异常",e);
        }
        return meta;
    }



    @GetMapping("/fourManagement/Capa/pagedQuery")
    public CapaResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                   @RequestParam(value = "pageSize") Integer pageSize,
                                   @RequestParam(value = "year", required = false) String year){
        CapaResponse response = new CapaResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            CapaResponse capaResponse = capacityReService.pagedQuery(pageNum, pageSize,year);
            if (capaResponse != null){
                return capaResponse;
            }
        }catch (Exception e){
            logger.error("获取库容复核分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取库容复核分页数据失败");
        return response;

    }




    @PostMapping("/fourManagement/Capa/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody CapaUnit unit,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = capacityReService.previewById(unit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(capacityReService.getFileNameById(unit.getId()), "UTF-8");


            //设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,"inline; filename*=UTF-8'' " + encodeFilename);//inline,attachment


            headers.setContentType(MediaType.APPLICATION_PDF);
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


}
