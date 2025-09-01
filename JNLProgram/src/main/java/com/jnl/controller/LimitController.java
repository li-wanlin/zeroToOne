package com.jnl.controller;

import com.jnl.service.impl.LimitServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.perDelimitVo.LimitResponse;
import com.jnl.vo.perDelimitVo.LimitUnit;
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
public class LimitController {


    @Resource
    LimitServiceImpl limitService;

    private static final Logger logger = LoggerFactory.getLogger(LimitController.class);



    @PostMapping("/fourManagement/Limit/insertByInfo")
    public Meta insertByInfo(LimitUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入确权划界数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = limitService.insertByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入确权划界数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入确权划界数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourManagement/Limit/deleteByInfo")
    public Meta deleteByInfo(@RequestBody LimitUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除确权划界数据失败");
        try {
            Boolean delete = limitService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除确权划界数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除确权划界数据成功Controller层发生异常",e);
        }
        return meta;
    }



    @PutMapping("/fourManagement/Limit/updateByInfo")
    public Meta updateByInfo(LimitUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新确权划界数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = limitService.updateByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新确权划界数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("确权划界更新数据Controller层发生异常",e);
        }
        return meta;
    }


    @GetMapping("/fourManagement/Limit/pagedQuery")
    public LimitResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "year", required = false) String year){
        LimitResponse response = new LimitResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            LimitResponse limitResponse = limitService.pagedQuery(pageNum, pageSize,year);
            if (limitResponse != null){
                return limitResponse;
            }
        }catch (Exception e){
            logger.error("获取确权划界分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取确权划界分页数据失败");
        return response;

    }

    @PostMapping("/fourManagement/Limit/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody LimitUnit unit,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = limitService.previewById(unit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(limitService.getFileNameById(unit.getId()), "UTF-8");


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
