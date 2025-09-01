package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.LawPatrolImg;
import com.jnl.entity.UNSInfo;
import com.jnl.mapper.LawPatrolImgMapper;
import com.jnl.mapper.LawPatrolMapper;
import com.jnl.service.LawPatrolImgService;
import com.jnl.utils.DateLocalUtils;
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
public class LawPatrolImgServiceImpl extends ServiceImpl<LawPatrolImgMapper, LawPatrolImg> implements LawPatrolImgService {


    @Resource
    LawPatrolImgMapper lawPatrolImgMapper;

    @Resource
    ImagesInfoServiceImpl imagesInfoService;


    @Value("${image.upload.lawPatrol}")
    private String lawPatrolImage;


    private static final Logger logger = LoggerFactory.getLogger(LawPatrolImgServiceImpl.class);



    @Override
    public Boolean insertByInfo(Integer lawId, MultipartFile[] files) {
        try {
            if (lawId == null || files == null){
                return false;
            }

            List<LawPatrolImg> infos = new ArrayList<>();

            for (MultipartFile file:files) {
                ImageGeneralInfo generalInfo = imagesInfoService.generalUpload(file, lawPatrolImage);
                if (!generalInfo.getIfCom()){
                    continue;
                }
                LawPatrolImg info = new LawPatrolImg();
                info.setLawId(lawId);
                info.setFileName(generalInfo.getFileName());
                info.setFilePath(generalInfo.getFilePath());
                info.setUpdateTime(new Date());
                infos.add(info);
            }

            return saveBatch(infos);
        }catch (Exception e){
            logger.error("插入执法巡查图片数据发生异常",e);
            throw new RuntimeException("插入失败", e);
        }
    }

    @Override
    public List<String> getFileNames(Integer lawId) {
        try {
            QueryWrapper<LawPatrolImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("law_id",lawId);

            List<LawPatrolImg> lawPatrolImgs = lawPatrolImgMapper.selectList(queryWrapper);
            if (lawPatrolImgs == null || lawPatrolImgs.size() == 0){
                return new ArrayList<String>();
            }

            return lawPatrolImgs.stream().map(LawPatrolImg::getFileName).distinct().collect(Collectors.toList());
        }catch (Exception e){
            logger.error("获取该id下数据发生异常",e);
        }
        return null;
    }

    @Override
    public void deleteByLawId(Integer lawId) {
        try {
            QueryWrapper<LawPatrolImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("law_id",lawId);

            List<LawPatrolImg> lawPatrolImgs = lawPatrolImgMapper.selectList(queryWrapper);
            if (lawPatrolImgs == null || lawPatrolImgs.size() == 0){
                return ;
            }



            List<String> filePaths = lawPatrolImgs.stream().map(LawPatrolImg::getFilePath).collect(Collectors.toList());
            imagesInfoService.batchDeleteFiles(filePaths);


            for (LawPatrolImg law:lawPatrolImgs) {
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
            QueryWrapper<LawPatrolImg> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_name",fileName);
            queryWrapper.orderByDesc("update_time");

            List<LawPatrolImg> lawPatrolImgs = lawPatrolImgMapper.selectList(queryWrapper);

            if (lawPatrolImgs != null && lawPatrolImgs.size() > 0){
                return lawPatrolImgs.get(0).getFilePath();
            }

        }catch (Exception e){
            logger.error("通过文件名获取路径出错",e);
        }
        return null;
    }
}
