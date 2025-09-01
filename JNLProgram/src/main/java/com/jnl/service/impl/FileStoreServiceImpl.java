package com.jnl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FileStore;
import com.jnl.mapper.FileStoreMapper;
import com.jnl.service.FileStoreService;
import com.jnl.vo.fileVo.GeneralInfo;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
public class FileStoreServiceImpl extends ServiceImpl<FileStoreMapper, FileStore> implements FileStoreService {



    private static final Logger logger = LoggerFactory.getLogger(FileStoreServiceImpl.class);



    @Override
    public GeneralInfo generalUpload(MultipartFile[] files, String path, Integer relateId, String relateType) {
        GeneralInfo info = new GeneralInfo();
        info.setIfCom(false);
        if (files.length == 0) {
            return info;
        }



        try {
            List<FileStore> fileStores = new ArrayList<>();

            for (MultipartFile file:files) {
                GeneralInfo uploadFile = uploadFile(file, path);
                FileStore fileStore = new FileStore();
                fileStore.setRelateId(relateId);
                fileStore.setRelateType(relateType);
                fileStore.setFileName(uploadFile.getFileName());
                fileStore.setFilePath(uploadFile.getFilePath());

                fileStores.add(fileStore);
            }

            saveBatch(fileStores);

            info.setIfCom(true);

        } catch (Exception e) {
            logger.error("上传图片发生异常",e);
            throw new RuntimeException("插入失败", e);
        }
        return info;
    }

    @Override
    public GeneralInfo singleDeleteFile(Integer relateId, String relateType, String fileName) {
        return null;
    }

    @Override
    public GeneralInfo batchDeleteFile(Integer relateId, String relateType) {
        return null;
    }

    @Override
    public byte[] generalPreview(Integer relateId, String relateType, String fileName) {
        return new byte[0];
    }




    public GeneralInfo uploadFile(MultipartFile file, String path){
        GeneralInfo info = new GeneralInfo();
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
            boolean pdf = !fileName.toLowerCase().endsWith("pdf");

            if (png && jpg && jpeg && gif && pdf){
                return info;
            }


            Path filePath = Paths.get(path, fileName);
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
