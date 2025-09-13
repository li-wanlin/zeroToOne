package com.stg.controller;


import com.stg.entity.PDFInfo;
import com.stg.service.impl.PDFInfoServiceImpl;
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
public class PDFInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PDFInfoController.class);


    @Resource
    PDFInfoServiceImpl pdfInfoService;


    @PostMapping("/pdf/upload")
    public Meta uploadPDF(@RequestParam("file") MultipartFile file,@RequestParam("belongTo") String belongTo,
                          @RequestParam(value = "catalogNumber",required = false) Integer catalogNumber,
                          @RequestParam(value = "fileNameReq",required = false) String fileNameReq){
        Meta meta = new Meta();
        try {
            PDFInfo pdfInfo = pdfInfoService.uploadPDF(file,belongTo,catalogNumber,fileNameReq);
            if (pdfInfo == null || pdfInfo.getId() == null){
                meta.setStatus(400);
                meta.setMsg("PDF文件上传失败，请检查");
                return meta;
            }
            meta.setStatus(200);
            meta.setMsg("PDF文件上传成功");
            return meta;
        }catch (Exception e){
            logger.error("PDF文件上传发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("PDF文件上传失败，请检查");
        return meta;

    }



    @GetMapping("/pdf/download/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> downloalPDF(@PathVariable String filename,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }
            byte[] fileBytes = pdfInfoService.downloadPDF(filename, startByte);

            org.springframework.core.io.Resource resource = new ByteArrayResource(fileBytes);

            //对文件名进行编码
            String encodeFilename = URLEncoder.encode(filename, "UTF-8");


            //设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,"inline; filename*=UTF-8'' " + encodeFilename);


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
