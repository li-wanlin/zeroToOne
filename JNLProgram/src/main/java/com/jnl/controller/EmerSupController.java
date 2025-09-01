package com.jnl.controller;

import com.jnl.service.impl.EmerSupServiceImpl;
import com.jnl.vo.emerVo.EmerResponse;
import com.jnl.vo.emerVo.EmerUnit;
import com.jnl.vo.functionVo.Meta;
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
public class EmerSupController {


    @Resource
    EmerSupServiceImpl emerSupService;

    private static final Logger logger = LoggerFactory.getLogger(EmerSupController.class);



    @GetMapping("/fourManagement/EmerSup/selectLatestTen")
    public EmerResponse selectLatestTen(){
        EmerResponse response = new EmerResponse();
        Meta meta = new Meta();

        response.setMeta(meta);
        try {
            List<EmerUnit> units = emerSupService.selectLatestTen();
            if (units != null){
                meta.setStatus(200);
                meta.setMsg("获取应急保障分页数据成功");
                response.setEmerUnits(units);
                return response;
            }
        }catch (Exception e){
            logger.error("获取应急保障分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取应急保障分页数据失败");
        return response;
    }




    @PostMapping("/fourManagement/EmerSup/insertByInfo")
    public Meta insertByInfo(EmerUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入应急保障数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = emerSupService.insertByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入应急保障数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入应急保障数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourManagement/EmerSup/deleteByInfo")
    public Meta deleteByInfo(@RequestBody EmerUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除应急保障数据失败");
        try {
            Boolean delete = emerSupService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除应急保障数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除应急保障数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourManagement/EmerSup/updateByInfo")
    public Meta updateByInfo(EmerUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新应急保障数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = emerSupService.updateByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新应急保障数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("应急保障更新数据Controller层发生异常",e);
        }
        return meta;
    }



    @GetMapping("/fourManagement/EmerSup/pagedQuery")
    public EmerResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                   @RequestParam(value = "pageSize") Integer pageSize,
                                   @RequestParam(value = "year", required = false) String year){
        EmerResponse response = new EmerResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            EmerResponse emerResponse = emerSupService.pagedQuery(pageNum, pageSize,year);
            if (emerResponse != null){
                return emerResponse;
            }
        }catch (Exception e){
            logger.error("获取应急保障分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取应急保障分页数据失败");
        return response;

    }




    @PostMapping("/fourManagement/EmerSup/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody EmerUnit unit,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = emerSupService.previewById(unit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(emerSupService.getFileNameById(unit.getId()), "UTF-8");


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
