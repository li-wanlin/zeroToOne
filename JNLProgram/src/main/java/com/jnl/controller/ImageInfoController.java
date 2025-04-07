package com.jnl.controller;

import com.jnl.entity.ImagesInfo;
import com.jnl.sevice.impl.ImagesInfoServiceImpl;
import com.jnl.vo.imageInfoVo.ImageResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageInfoController {


    @Value("${image.upload.path}")
    private String uploadPath;

    @Resource
    ImagesInfoServiceImpl imagesInfoService;

    private static final Logger logger = LoggerFactory.getLogger(ImageInfoController.class);


    @PostMapping("/image/uploadImage")
    public String uploadImage(@RequestParam("image") MultipartFile file){
        if (file.isEmpty()) {
            return "The file is empty";
        }
        try {
            String fileName = file.getOriginalFilename();
            //用相对路径，后发现项目打包后会清空数据，故需用绝对路径下的外部目录
/*            ClassPathResource resource = new ClassPathResource("");
            File rootDir = resource.getFile();*/
            Path filePath = Paths.get(uploadPath, fileName);
            File directory = filePath.getParent().toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Files.write(filePath, file.getBytes());

            ImagesInfo imagesInfo = new ImagesInfo();
            imagesInfo.setFileName(fileName);
            imagesInfo.setFilePath(filePath.toString());
            boolean save = imagesInfoService.save(imagesInfo);
            if (save){
                return "upload success";
            }else {
                return "upload failed";
            }
        } catch (Exception e) {
            logger.error("upload failed",e);
            return "upload failed";
        }
    }


    @GetMapping("/image/downloadImage")
    @ResponseBody
    public void downloadImage(HttpServletResponse response){

        try {
            ImagesInfo imagesInfo = imagesInfoService.getById(6);
            Path filePath = Paths.get(imagesInfo.getFilePath());
            byte[] imageBytes = Files.readAllBytes(filePath);

            // 设置响应头信息
            // 设置 Content-Type 头
            response.setContentType(this.getContentType(filePath));
            // 设置 Content-Length 头
            response.setContentLength(imageBytes.length);
            // 设置 Cache-Control 头
            response.setHeader("Cache-Control", "max-age=3600");

            // 将图片字节数组写入响应输出流
            response.getOutputStream().write(imageBytes);
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            logger.error("downloadImage failed",e);
            // 设置响应状态码为 500
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    //根据文件扩展名获取Content-Type
    private String getContentType(Path filePath){
        try{
            String fileName = filePath.getFileName().toString();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            switch(fileExtension){
                case "jpg":
                case "jpeg":
                    return MediaType.IMAGE_JPEG_VALUE;
                case "png":
                    return MediaType.IMAGE_PNG_VALUE;
                case "gif":
                    return MediaType.IMAGE_GIF_VALUE;
                default:
                    return MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
        }catch (Exception e){
            logger.error("获取图片扩展名时发生错误",e);
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }



}
