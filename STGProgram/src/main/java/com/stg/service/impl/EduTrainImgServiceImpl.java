package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.EduTrainImg;
import com.stg.mapper.EduTrainImgMapper;
import com.stg.service.EduTrainImgService;
import com.stg.vo.imageInfoVo.ImageGeneralInfo;
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
public class EduTrainImgServiceImpl extends ServiceImpl<EduTrainImgMapper, EduTrainImg> implements EduTrainImgService {

    @Resource
    EduTrainImgMapper eduTrainImgMapper;

    @Resource
    ImagesInfoServiceImpl imagesInfoService;


    @Value("${image.upload.eduTrain}")
    private String eduTrainImage;


    private static final Logger logger = LoggerFactory.getLogger(EduTrainImgServiceImpl.class);





    @Override
    public Boolean insertByInfo(Integer imgId, MultipartFile[] files) {
        try {
            if (imgId == null || files == null){
                return false;
            }

            List<EduTrainImg> infos = new ArrayList<>();

            for (MultipartFile file:files) {
                ImageGeneralInfo generalInfo = imagesInfoService.generalUpload(file, eduTrainImage);
                if (!generalInfo.getIfCom()){
                    continue;
                }
                EduTrainImg info = new EduTrainImg();
                info.setImgId(imgId);
                info.setFileName(generalInfo.getFileName());
                info.setFilePath(generalInfo.getFilePath());
                info.setUpdateTime(new Date());
                infos.add(info);
            }

            return saveBatch(infos);
        }catch (Exception e){
            logger.error("插入教育培训图片数据发生异常",e);
            throw new RuntimeException("插入失败", e);
        }
    }




    @Override
    public List<String> getFileNames(Integer imgId) {
        try {
            QueryWrapper<EduTrainImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("img_id",imgId);

            List<EduTrainImg> imgs = eduTrainImgMapper.selectList(queryWrapper);
            if (imgs == null || imgs.size() == 0){
                return new ArrayList<String>();
            }

            return imgs.stream().map(EduTrainImg::getFileName).distinct().collect(Collectors.toList());
        }catch (Exception e){
            logger.error("获取该id下数据发生异常",e);
        }
        return null;
    }

    @Override
    public void deleteByImgId(Integer imgId) {
        try {
            QueryWrapper<EduTrainImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("img_id",imgId);

            List<EduTrainImg> imgs = eduTrainImgMapper.selectList(queryWrapper);
            if (imgs == null || imgs.size() == 0){
                return ;
            }



            List<String> filePaths = imgs.stream().map(EduTrainImg::getFilePath).collect(Collectors.toList());
            imagesInfoService.batchDeleteFiles(filePaths);


            for (EduTrainImg law:imgs) {
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
            QueryWrapper<EduTrainImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_name",fileName);
            queryWrapper.orderByDesc("update_time");

            List<EduTrainImg> Imgs = eduTrainImgMapper.selectList(queryWrapper);

            if (Imgs != null && Imgs.size() > 0){
                return Imgs.get(0).getFilePath();
            }

        }catch (Exception e){
            logger.error("通过文件名获取路径出错",e);
        }
        return null;
    }

}
