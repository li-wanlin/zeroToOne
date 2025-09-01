package com.jnl.controller;


import com.jnl.service.impl.CheckServiceImpl;
import com.jnl.vo.capacityVo.CapaUnit;
import com.jnl.vo.checkVo.CheckResponse;
import com.jnl.vo.checkVo.CheckUnit;
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

@RestController
public class CheckController {

    @Resource
    CheckServiceImpl checkService;


    private static final Logger logger = LoggerFactory.getLogger(CheckController.class);



    @PostMapping("/fourManagement/Check/insertByInfo")
    public Meta insertByInfo(CheckUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入隐患排查数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = checkService.insertByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入隐患排查数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入隐患排查数据Controller层发生异常",e);
        }
        return meta;
    }



    @DeleteMapping("/fourManagement/Check/deleteByInfo")
    public Meta deleteByInfo(@RequestBody CheckUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除隐患排查数据失败");
        try {
            Boolean delete = checkService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除隐患排查数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除隐患排查数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourManagement/Check/updateByInfo")
    public Meta updateByInfo(CheckUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新隐患排查数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = checkService.updateByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新隐患排查数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("隐患排查更新数据Controller层发生异常",e);
        }
        return meta;
    }



    @GetMapping("/fourManagement/Check/pagedQuery")
    public CheckResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                    @RequestParam(value = "pageSize") Integer pageSize,
                                    @RequestParam(value = "year", required = false) String year){
        CheckResponse response = new CheckResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            CheckResponse checkResponse = checkService.pagedQuery(pageNum, pageSize,year);
            if (checkResponse != null){
                return checkResponse;
            }
        }catch (Exception e){
            logger.error("获取隐患排查分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取隐患排查分页数据失败");
        return response;

    }




    @PostMapping("/fourManagement/Check/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody CapaUnit unit,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = checkService.previewById(unit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(checkService.getFileNameById(unit.getId()), "UTF-8");


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
