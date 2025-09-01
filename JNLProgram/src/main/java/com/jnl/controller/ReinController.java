package com.jnl.controller;

import com.jnl.service.impl.ReinServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.reinVo.ReinResponse;
import com.jnl.vo.reinVo.ReinUnit;
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
public class ReinController {

    @Resource
    ReinServiceImpl reinService;


    private static final Logger logger = LoggerFactory.getLogger(ReinController.class);


    @PostMapping("/fourManagement/Rein/insertByInfo")
    public Meta insertByInfo(ReinUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("插入除险加固数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = reinService.insertByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("插入除险加固数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("插入除险加固数据Controller层发生异常",e);
        }
        return meta;
    }


    @DeleteMapping("/fourManagement/Rein/deleteByInfo")
    public Meta deleteByInfo(@RequestBody ReinUnit unit){
        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("删除除险加固数据失败");
        try {
            Boolean delete = reinService.deleteByInfo(unit);
            if (delete != null && delete){
                meta.setStatus(200);
                meta.setMsg("删除除险加固数据成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("删除除险加固数据成功Controller层发生异常",e);
        }
        return meta;
    }


    @PutMapping("/fourManagement/Rein/updateByInfo")
    public Meta updateByInfo(ReinUnit unit,
                             @RequestParam(value = "file",required = false) MultipartFile file){

        Meta meta = new Meta();
        meta.setStatus(400);
        meta.setMsg("更新除险加固数据失败");
        try {
            if (unit == null && file == null){
                return meta;
            }

            Boolean insert = reinService.updateByInfo(unit, file);
            if (insert != null && insert){
                meta.setStatus(200);
                meta.setMsg("更新除险加固数据成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("除险加固更新数据Controller层发生异常",e);
        }
        return meta;
    }


    @GetMapping("/fourManagement/Rein/pagedQuery")
    public ReinResponse pagedQuery(@RequestParam(value = "pageNum") Integer pageNum,
                                   @RequestParam(value = "pageSize") Integer pageSize){
        ReinResponse response = new ReinResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            ReinResponse reinResponse = reinService.pagedQuery(pageNum, pageSize);
            if (reinResponse != null){
                return reinResponse;
            }
        }catch (Exception e){
            logger.error("获取除险加固分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取除险加固分页数据失败");
        return response;

    }




    @PostMapping("/fourManagement/Rein/previewById")
    public ResponseEntity<org.springframework.core.io.Resource> previewById(@RequestBody ReinUnit unit,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }


            byte[] fileBytes = reinService.previewById(unit.getId(), startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(reinService.getFileNameById(unit.getId()), "UTF-8");


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
