package com.stg.controller;

import com.stg.entity.PDFInfo;
import com.stg.service.impl.PDFSystemServiceImpl;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.pdfVo.PDFInfoResponse;
import com.stg.vo.pdfVo.PDFUnit;
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
public class PDFSystemController {

    @Resource
    PDFSystemServiceImpl pdfSystemService;


    private static final Logger logger = LoggerFactory.getLogger(PDFSystemController.class);


    @GetMapping("/fourGovern/PDFSystem/selectNameAndBelongTo")
    public PDFInfoResponse selectNameAndBelongTo(){
        PDFInfoResponse response = new PDFInfoResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<PDFUnit> pdfUnits = pdfSystemService.selectNameAndBelongTo();
            if (pdfUnits != null && pdfUnits.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取制度管理局部数据成功");
                response.setPdfUnits(pdfUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取制度管理局部数据Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取制度管理局部数据失败");
        return response;
    }


    @GetMapping("/fourGovern/PDFSystem/PDFPreview/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> PDFPreview(@PathVariable String filename,
                                                                            @RequestHeader(value = "Range", required = false) String rangeHeader){

        try{
            long startByte = 0;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")){
                String[] range = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(range[0]);
            }
            byte[] fileBytes = pdfSystemService.PDFPreview(filename, startByte);

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


    @GetMapping("/fourGovern/PDFSystem/selectByBelongTo")
    public PDFInfoResponse selectByBelongTo(@RequestParam(value = "pageNum") Integer pageNum,
                                            @RequestParam(value = "pageSize") Integer pageSize,
                                            @RequestParam(value = "belongTo") String belongTo){

        Meta meta = new Meta();


        try {
            PDFInfoResponse pdfInfoResponse = pdfSystemService.selectByBelongTo(pageNum, pageSize, belongTo);
            if (pdfInfoResponse != null && pdfInfoResponse.getTotalPage() > 0){
                meta.setStatus(200);
                meta.setMsg("获取类名下文件名成功");
                pdfInfoResponse.setMeta(meta);
                return pdfInfoResponse;
            }
        }catch (Exception e){
            logger.error("获取类名下文件名Controller层发生异常",e);
        }
        PDFInfoResponse response = new PDFInfoResponse();
        meta.setStatus(400);
        meta.setMsg("获取类名下文件名失败");
        response.setMeta(meta);
        return response;
    }



    @PostMapping("/fourGovern/PDFSystem/uploadPDF")
    public Meta uploadPDF(@RequestParam("file") MultipartFile file, @RequestParam("belongTo") String belongTo,
                          @RequestParam(value = "fileNameReq",required = false) String fileNameReq){
        Meta meta = new Meta();
        try {
            Boolean uploadPDF = pdfSystemService.uploadPDF(file, belongTo,fileNameReq);
            if (uploadPDF){
                meta.setStatus(200);
                meta.setMsg("新增或替换制度管理PDF文件成功");
                return meta;
            }
        }catch (Exception e){
            logger.error("新增或替换制度管理PDF文件Controller层发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("新增或替换制度管理PDF文件失败");
        return meta;
    }


    @DeleteMapping("/fourGovern/PDFSystem/deleteByInfo")
    public Meta deleteByInfo(@RequestBody PDFInfo pdfInfo){
        Meta meta = new Meta();
        try {
            String belongTo = pdfInfo.getBelongTo();
            String fileName = pdfInfo.getFileName();
            Boolean delete = pdfSystemService.deleteByInfo(belongTo, fileName);
            if (delete){
                meta.setStatus(200);
                meta.setMsg("删除制度管理PDF文件信息成功");
                return meta;
            }

        }catch (Exception e){
            logger.error("删除制度管理PDF文件信息Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("删除制度管理PDF文件信息失败");
        return meta;
    }



}
