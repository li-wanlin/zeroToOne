package com.stg.controller;

import com.stg.entity.ImagesInfo;
import com.stg.service.impl.ImagesInfoServiceImpl;
import com.stg.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

@RestController
public class ImageInfoController {


    @Value("${image.upload.path}")
    private String imageUploadPath;




    @Resource
    ImagesInfoServiceImpl imagesInfoService;

    private static final Logger logger = LoggerFactory.getLogger(ImageInfoController.class);


    @PostMapping("/image/uploadImage")
    public Meta uploadImage(@RequestParam("image") MultipartFile file){

        Meta meta = new Meta();
        try{

            ImagesInfo imagesInfo = imagesInfoService.uploadImage(file);
            if (imagesInfo == null || imagesInfo.getFilePath() == null){
                meta.setStatus(400);
                meta.setMsg("图片上传失败，请检查");
                return meta;
            }
            meta.setStatus(200);
            meta.setMsg("图片上传成功");
            return meta;

        }catch (Exception e){
            logger.error("上传图片时发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("图片上传失败，请检查");
        return meta;
    }


    @GetMapping("/image/downloadImage/{imageName}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadImage(@PathVariable String imageName){

        //暂时根据图片名称去对应位置下载图片
        try {
            ImagesInfo imagesInfo = imagesInfoService.downloadImage(imageName);

            if (imagesInfo == null || imagesInfo.getId() == null){
                return ResponseEntity.notFound().build();
            }

            File file = new File(imagesInfo.getFilePath());
            org.springframework.core.io.Resource resource = new FileSystemResource(file);


            //设置响应头
            HttpHeaders headers = new HttpHeaders();

            // 设置 Content-Disposition 头，指定文件以附件形式下载
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; imageName=" + imagesInfo.getFileName());


            //设置Content-Type 头
            headers.setContentType(this.getContentType(imagesInfo.getFileName()));

            // 设置 Content-Length 头
            headers.setContentLength(file.length());

            //设置 Cache-Control 头,缓存时间
            Integer cache = 60 * 60 * 24 * 30;
            headers.setCacheControl("public, max-age=" + cache.toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            logger.error("downloadImage failed",e);
            // 设置响应状态码为 404
            return ResponseEntity.notFound().build();
        }
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
