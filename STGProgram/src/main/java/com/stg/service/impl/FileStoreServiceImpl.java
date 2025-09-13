package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.FileStore;
import com.stg.mapper.FileStoreMapper;
import com.stg.service.FileStoreService;
import com.stg.vo.fileVo.FilePreResponse;
import com.stg.vo.fileVo.FileUnit;
import com.stg.vo.fileVo.GeneralInfo;
import com.stg.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class FileStoreServiceImpl extends ServiceImpl<FileStoreMapper, FileStore> implements FileStoreService {


    @Resource
    FileStoreMapper fileStoreMapper;



    private static final Logger logger = LoggerFactory.getLogger(FileStoreServiceImpl.class);



    @Override
    public GeneralInfo generalUpload(MultipartFile[] files, String path, Integer relateId, String relateType) {
        GeneralInfo info = new GeneralInfo();
        info.setIfCom(false);
        if (files.length == 0 || path == null || relateId == null || relateType == null) {
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
                fileStore.setUpdateTime(new Date());

                fileStores.add(fileStore);
            }

            //saveBatch(fileStores);


            for (FileStore fileStore:fileStores) {
                saveOrUpdate(fileStore,new LambdaUpdateWrapper<FileStore>()
                        .eq(FileStore::getRelateId,fileStore.getRelateId()).eq(FileStore::getFilePath,fileStore.getFilePath()));
            }


            info.setIfCom(true);

        } catch (Exception e) {
            logger.error("上传图片发生异常",e);
            throw new RuntimeException("插入失败", e);
        }
        return info;
    }

    @Override
    public GeneralInfo generalUploadParam(MultipartFile[] files, String path, Integer relateId, String relateType, String nameParam) {
        GeneralInfo info = new GeneralInfo();
        info.setIfCom(false);
        if (files.length == 0 || path == null || relateId == null || relateType == null) {
            return info;
        }



        try {
            List<FileStore> fileStores = new ArrayList<>();

            for (MultipartFile file:files) {
                GeneralInfo uploadFile = uploadFileParam(file, path,nameParam);
                FileStore fileStore = new FileStore();
                fileStore.setRelateId(relateId);
                fileStore.setRelateType(relateType);
                fileStore.setFileName(uploadFile.getFileName());
                fileStore.setFilePath(uploadFile.getFilePath());
                fileStore.setUpdateTime(new Date());

                fileStores.add(fileStore);
            }

            //saveBatch(fileStores);


            for (FileStore fileStore:fileStores) {
                saveOrUpdate(fileStore,new LambdaUpdateWrapper<FileStore>()
                        .eq(FileStore::getRelateId,fileStore.getRelateId()).eq(FileStore::getFilePath,fileStore.getFilePath()));
            }


            info.setIfCom(true);

        } catch (Exception e) {
            logger.error("上传图片发生异常",e);
            throw new RuntimeException("插入失败", e);
        }
        return info;
    }

    @Override
    public GeneralInfo singleDeleteFile(Integer relateId, String relateType, String fileName) {
        GeneralInfo info = new GeneralInfo();
        info.setIfCom(false);
        if (relateId == null || relateType == null || fileName == null) {
            return info;
        }

        try {
            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("relateId",relateId);
            wrapper.eq("relateType",relateType);
            wrapper.eq("file_name",fileName);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() == 0){
                info.setIfCom(true);
                return info;
            }

            FileStore store = fileStores.get(0);
            deleteFile(store.getFilePath());

            removeById(store.getId());

            info.setIfCom(true);


        }catch (Exception e){
            logger.error("上传图片发生异常",e);
            throw new RuntimeException("插入失败", e);
        }

        return info;
    }

    @Override
    public GeneralInfo batchDeleteFile(Integer relateId, String relateType) {
        GeneralInfo info = new GeneralInfo();
        info.setIfCom(false);
        if (relateId == null || relateType == null) {
            return info;
        }

        try {
            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("relateId",relateId);
            wrapper.eq("relateType",relateType);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() == 0){
                info.setIfCom(true);
                return info;
            }


            for (FileStore store:fileStores) {
                deleteFile(store.getFilePath());
                removeById(store.getId());
            }


            info.setIfCom(true);


        }catch (Exception e){
            logger.error("上传图片发生异常",e);
            throw new RuntimeException("插入失败", e);
        }

        return info;
    }

    @Override
    public byte[] generalPreview(Integer relateId, String relateType, String fileName) {

        if (relateId == null || relateType == null || fileName == null) {
            return null;
        }


        try{

            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("relateId",relateId);
            wrapper.eq("relateType",relateType);
            wrapper.eq("file_name",fileName);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() == 0){
                return null;
            }

            FileStore store = fileStores.get(0);

            Path path = Paths.get(store.getFilePath());
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
    public GeneralInfo singleDeleteNew(Integer id, String fileName) {
        GeneralInfo info = new GeneralInfo();
        info.setIfCom(false);
        if (id == null || fileName == null) {
            return info;
        }

        try {
            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("id",id);
            wrapper.eq("file_name",fileName);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() == 0){
                info.setIfCom(true);
                return info;
            }

            FileStore store = fileStores.get(0);
            deleteFile(store.getFilePath());

            removeById(store.getId());

            info.setIfCom(true);


        }catch (Exception e){
            logger.error("上传图片发生异常",e);
            throw new RuntimeException("插入失败", e);
        }

        return info;
    }

    @Override
    public byte[] generalPreviewNew(Integer id, String fileName) {
        if (id == null || fileName == null) {
            return null;
        }


        try{

            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("id",id);
            wrapper.eq("file_name",fileName);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() == 0){
                return null;
            }

            FileStore store = fileStores.get(0);

            Path path = Paths.get(store.getFilePath());
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
    public FilePreResponse getFileName(Integer relateId, String relateType) {
        FilePreResponse response = new FilePreResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        meta.setStatus(400);
        meta.setMsg("获取文件名失败");

        if (relateId == null || relateType == null) {
            return response;
        }


        try{

            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("relateId",relateId);
            wrapper.eq("relateType",relateType);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() == 0){
                meta.setStatus(400);
                meta.setMsg("该行无关联文件");
                return response;
            }

            List<FileUnit> pdfs = new ArrayList<>();
            List<FileUnit> pictures = new ArrayList<>();

            for (FileStore fileStore:fileStores) {
                FileUnit unit = new FileUnit();
                unit.setId(fileStore.getId());
                unit.setFileName(fileStore.getFileName());
                if (fileStore.getFileName().toLowerCase().endsWith("pdf")){
                    pdfs.add(unit);
                }else {
                    pictures.add(unit);
                }
            }

            meta.setStatus(200);
            meta.setMsg("获取文件名成功");

            response.setPdfs(pdfs);
            response.setPictures(pictures);

            return response;


        }catch (Exception e){
            logger.error("在service中下载文件发生异常");
        }

        return response;
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










    public GeneralInfo uploadFileParam(MultipartFile file, String path, String nameParam){
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






            if (png && jpg && jpeg && gif){
                return info;
            }


            nameParam = nameParam + fileName.toLowerCase().substring(fileName.length()-4);


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
            info.setFileName(nameParam);
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

            QueryWrapper<FileStore> wrapper = new QueryWrapper<>();
            wrapper.eq("file_path",absolutePath);

            List<FileStore> fileStores = fileStoreMapper.selectList(wrapper);
            if (fileStores == null || fileStores.size() != 1){
                return false;
            }


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
