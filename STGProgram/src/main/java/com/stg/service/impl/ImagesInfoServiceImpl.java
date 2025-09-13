package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.ImagesInfo;
import com.stg.mapper.ImagesInfoMapper;
import com.stg.service.ImagesInfoService;
import com.stg.vo.imageInfoVo.ImageGeneralInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImagesInfoServiceImpl extends ServiceImpl<ImagesInfoMapper, ImagesInfo> implements ImagesInfoService {

    @Value("${image.upload.path}")
    private String imageUploadPath;

    @Resource
    ImagesInfoMapper imagesInfoMapper;


    private static final Logger logger = LoggerFactory.getLogger(ImagesInfoServiceImpl.class);


    @Override
    public ImagesInfo uploadImage(MultipartFile file) {
        ImagesInfo info = new ImagesInfo();
        if (file.isEmpty()) {
            return info;
        }
        try {
            String fileName = file.getOriginalFilename();
            //用相对路径，后发现项目打包后会清空数据，故需用绝对路径下的外部目录
/*            ClassPathResource resource = new ClassPathResource("");
            File rootDir = resource.getFile();*/

            boolean png = !fileName.toLowerCase().endsWith("png");
            boolean jpg = !fileName.toLowerCase().endsWith("jpg");
            boolean jpeg = !fileName.toLowerCase().endsWith("jpeg");
            boolean gif = !fileName.toLowerCase().endsWith("gif");

            if (png && jpg && jpeg && gif){
                return info;
            }


            Path filePath = Paths.get(imageUploadPath, fileName);
            File directory = filePath.getParent().toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            //通过Java中IO流，将文件写入输出流
            try(OutputStream os = new FileOutputStream(filePath.toFile())){
                os.write(file.getBytes());
            }



            //检查之前是否有相同名称文件，有文件则进行覆盖操作，没有文件则进行插入操作
            ImagesInfo imagesInfo = this.selectImageByName(fileName);

            if (imagesInfo != null && imagesInfo.getId() != null){
                info.setId(imagesInfo.getId());
            }

            //Files.write(filePath, file.getBytes());

            info.setFileName(fileName);
            info.setFilePath(filePath.toString());

            boolean save = saveOrUpdate(info);
            if (save){
                logger.info("上传图片至数据库成功");
            }else {
                logger.info("上传图片至数据库失败");
            }

            return info;
        } catch (Exception e) {
            logger.error("上传图片发生异常",e);
            return info;
        }
    }



    @Override
    public ImagesInfo selectImageByName(String imageName) {
        ImagesInfo imagesInfo = new ImagesInfo();
        try{
            if(StringUtils.isEmpty(imageName)){
                return imagesInfo;
            }

            QueryWrapper<ImagesInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("file_name",imageName);
            imagesInfo = getOne(wrapper);

            if (imagesInfo == null || imagesInfo.getId() == null){
                return imagesInfo;
            }

            return imagesInfo;

        }catch (Exception e){
            logger.error("查找图片信息发生异常",e);
        }

        return imagesInfo;
    }

    @Override
    public ImagesInfo downloadImage(String imageName) {
        try{
            ImagesInfo imagesInfo = this.selectImageByName(imageName);

            //根据图片名找不到文件，返回
            if (imagesInfo == null || imagesInfo.getId() == null){
                return null;
            }

            Path filePath = Paths.get(imagesInfo.getFilePath());
            File file = filePath.toFile();

            //存储的文件地址找不到文件，返回
            if (!file.exists()){
                return null;
            }

            return imagesInfo;

        }catch (Exception e){
            logger.error("下载图片发生异常",e);
        }

        return null;
    }



    @Override
    public ImageGeneralInfo generalUpload(MultipartFile file, String prefixPath) {
        ImageGeneralInfo info = new ImageGeneralInfo();
        info.setIfCom(false);
        if (file.isEmpty()) {
            return info;
        }
        try {
            String fileName = file.getOriginalFilename();
            //用相对路径，后发现项目打包后会清空数据，故需用绝对路径下的外部目录
/*            ClassPathResource resource = new ClassPathResource("");
            File rootDir = resource.getFile();*/

            boolean png = !fileName.toLowerCase().endsWith("png");
            boolean jpg = !fileName.toLowerCase().endsWith("jpg");
            boolean jpeg = !fileName.toLowerCase().endsWith("jpeg");
            boolean gif = !fileName.toLowerCase().endsWith("gif");

            if (png && jpg && jpeg && gif){
                return info;
            }


            Path filePath = Paths.get(prefixPath, fileName);
            File directory = filePath.getParent().toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            //通过Java中IO流，将文件写入输出流
            try(OutputStream os = new FileOutputStream(filePath.toFile())){
                os.write(file.getBytes());
            }

            info.setIfCom(true);
            info.setFileName(fileName);
            info.setFilePath(filePath.toString());

            return info;


        } catch (Exception e) {
            logger.error("上传图片发生异常",e);
        }
        return info;
    }

    @Override
    public byte[] generalPreview(String filePath) {
        try{
            Path path = Paths.get(filePath);
            File file = path.toFile();

            //存储的文件地址找不到文件，返回
            if (!file.exists()){
                return null;
            }

            return Files.readAllBytes(path);
        }catch (Exception e){
            logger.error("在service中下载文件发生异常");
        }
        return null;
    }




    @Override
    public void batchDeleteFiles(List<String> filePaths) {
        try {
            for (String path : filePaths) {
                deleteFile(path);
            }
        }catch (Exception e){
            logger.error("",e);
        }
    }



    /**
     * 删除单个文件（绝对路径）
     * @param absolutePath 文件绝对路径
     * @return 删除结果
     */
    public boolean deleteFile(String absolutePath) {
        if (absolutePath == null || absolutePath.isEmpty()) {
            logger.warn("文件路径为空，删除失败");
            return false;
        }

        try {
            // 1. 规范化路径（防目录遍历攻击）
            Path normalizedPath = Paths.get(absolutePath).normalize();
            String normalizedPathStr = normalizedPath.toString();


            // 4. 检查文件是否存在
            if (!Files.exists(normalizedPath)) {
                logger.warn("文件不存在: {}", normalizedPathStr);
                return false;
            }

            // 5. 检查是否为文件（非目录）
            if (!Files.isRegularFile(normalizedPath)) {
                logger.warn("路径不是文件: {}", normalizedPathStr);
                return false;
            }

            // 6. 执行删除
            Files.delete(normalizedPath);
            logger.info("文件删除成功: {}", normalizedPathStr);
            return true;
        } catch (Exception e) {
            logger.error("删除文件异常: {}", absolutePath, e);
            return false;
        }
    }



}
