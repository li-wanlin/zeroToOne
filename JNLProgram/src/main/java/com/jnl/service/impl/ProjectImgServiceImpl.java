package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.EduTrainImg;
import com.jnl.entity.ProjectImg;
import com.jnl.mapper.ProjectImgMapper;
import com.jnl.service.ProjectImgService;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectImgServiceImpl extends ServiceImpl<ProjectImgMapper, ProjectImg> implements ProjectImgService {

    @Resource
    ProjectImgMapper projectImgMapper;

    @Resource
    ImagesInfoServiceImpl imagesInfoService;


    @Value("${image.upload.project}")
    private String projectImage;


    private static final Logger logger = LoggerFactory.getLogger(ProjectImgServiceImpl.class);




    @Override
    public Boolean insertByInfo(Integer imgId, MultipartFile[] files) {
        try {
            if (imgId == null || files == null){
                return false;
            }

            List<ProjectImg> infos = new ArrayList<>();

            for (MultipartFile file:files) {
                ImageGeneralInfo generalInfo = imagesInfoService.generalUpload(file, projectImage);
                if (!generalInfo.getIfCom()){
                    continue;
                }
                ProjectImg info = new ProjectImg();
                info.setImgId(imgId);
                info.setFileName(generalInfo.getFileName());
                info.setFilePath(generalInfo.getFilePath());
                info.setUpdateTime(new Date());
                infos.add(info);
            }

            return saveBatch(infos);
        }catch (Exception e){
            logger.error("插入工程巡查图片数据发生异常",e);
            throw new RuntimeException("插入失败", e);
        }
    }

    @Override
    public List<String> getFileNames(Integer imgId) {
        try {
            QueryWrapper<ProjectImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("img_id",imgId);

            List<ProjectImg> imgs = projectImgMapper.selectList(queryWrapper);
            if (imgs == null || imgs.size() == 0){
                return new ArrayList<String>();
            }

            return imgs.stream().map(ProjectImg::getFileName).distinct().collect(Collectors.toList());
        }catch (Exception e){
            logger.error("获取该id下数据发生异常",e);
        }
        return null;
    }

    @Override
    public void deleteByImgId(Integer imgId) {
        try {
            QueryWrapper<ProjectImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("img_id",imgId);

            List<ProjectImg> imgs = projectImgMapper.selectList(queryWrapper);
            if (imgs == null || imgs.size() == 0){
                return ;
            }


            List<String> filePaths = imgs.stream().map(ProjectImg::getFilePath).collect(Collectors.toList());
            imagesInfoService.batchDeleteFiles(filePaths);


            for (ProjectImg law:imgs) {
                removeById(law.getId());
            }

        }catch (Exception e){
            logger.error("获取该id下数据发生异常",e);
        }
    }

    @Override
    public byte[] previewByName(String fileName) {
        try {
            if (fileName == null){
                return null;
            }

            String filePath = this.getPathByName(fileName);
            if (filePath == null){
                return null;
            }

            return imagesInfoService.generalPreview(filePath);

        }catch (Exception e){
            logger.error("在获取服务器文件时发生异常",e);
        }
        return null;
    }



    public String getPathByName(String fileName){
        try {
            if (fileName == null){
                return null;
            }
            QueryWrapper<ProjectImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_name",fileName);
            queryWrapper.orderByDesc("update_time");

            List<ProjectImg> Imgs = projectImgMapper.selectList(queryWrapper);

            if (Imgs != null && Imgs.size() > 0){
                return Imgs.get(0).getFilePath();
            }

        }catch (Exception e){
            logger.error("通过文件名获取路径出错",e);
        }
        return null;
    }



}
