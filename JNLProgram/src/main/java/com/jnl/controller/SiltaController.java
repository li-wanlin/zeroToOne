package com.jnl.controller;


import com.jnl.service.impl.SiltaServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.siltaVo.SiltaResponse;
import com.jnl.vo.siltaVo.SiltaUnit;
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
public class SiltaController {


    @Resource
    SiltaServiceImpl siltaService;


    private static final Logger logger = LoggerFactory.getLogger(SiltaController.class);



    @PostMapping("/fourManagement/Silta/insertByInfo")
    public Meta insertByInfo(SiltaUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入淤积治理数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = siltaService.insertByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入淤积治理数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入淤积治理数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourManagement/Silta/deleteByInfo")
    public Meta deleteByInfo(@RequestBody SiltaUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除淤积治理数据失败");
        try {
            Boolean delete = siltaService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除淤积治理数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除淤积治理数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourManagement/Silta/updateByInfo")
    public Meta updateByInfo(SiltaUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新淤积治理数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = siltaService.updateByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新淤积治理数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("淤积治理更新数据Controller层发生异常",e);
        }
        return meta;
    }



    @GetMapping("/fourManagement/Silta/pagedQuery")
    public SiltaResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize") Integer pageSize){
        SiltaResponse response = new SiltaResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            SiltaResponse siltaResponse = siltaService.pagedQuery(pageNum, pageSize);
            if (siltaResponse != null){
                return siltaResponse;
            }
        }catch (Exception e){
            logger.error("获取淤积治理分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取淤积治理分页数据失败");
        return response;

    }




    @PostMapping("/fourManagement/Silta/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody SiltaUnit unit,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = siltaService.previewById(unit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(siltaService.getFileNameById(unit.getId()), "UTF-8");


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
