package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.UNVInfo;
import com.jnl.mapper.UNVInfoMapper;
import com.jnl.service.UNVInfoService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UNVInfoServiceImpl extends ServiceImpl<UNVInfoMapper, UNVInfo> implements UNVInfoService {


    @Resource
    UNVInfoMapper unvInfoMapper;

    @Resource
    ImagesInfoServiceImpl imagesInfoService;

    @Value("${image.upload.unv}")
    private String unvImage;


    private static final Logger logger = LoggerFactory.getLogger(UNVInfoServiceImpl.class);



    @Override
    public Boolean insertByInfo(String inputTime, MultipartFile[] files) {
        try {
            if (inputTime == null || files == null){
                return false;
            }

            List<UNVInfo> infos = new ArrayList<>();

            for (MultipartFile file:files) {
                ImageGeneralInfo generalInfo = imagesInfoService.generalUpload(file, unvImage);
                if (!generalInfo.getIfCom()){
                    continue;
                }
                UNVInfo info = new UNVInfo();
                info.setInputTime(DateLocalUtils.parseGiveStrToDate(inputTime));
                info.setFileName(generalInfo.getFileName());
                info.setFilePath(generalInfo.getFilePath());
                info.setUpdateTime(new Date());
                infos.add(info);
            }

            return saveBatch(infos);
        }catch (Exception e){
            logger.error("插入无人机巡查数据发生异常",e);
        }
        return false;
    }




    @Override
    public List<String> getFileNames(String inputTime) {
        try {
            QueryWrapper<UNVInfo> queryWrapper = new QueryWrapper<>();
            Date strToDate = DateLocalUtils.parseGiveStrToDate(inputTime);
            queryWrapper.eq("input_time",strToDate);
            queryWrapper.orderByDesc("update_time");

            List<UNVInfo> unvInfos = unvInfoMapper.selectList(queryWrapper);
            if (unvInfos == null || unvInfos.size() == 0){
                return null;
            }

            return unvInfos.stream().map(UNVInfo::getFileName).distinct().collect(Collectors.toList());
        }catch (Exception e){
            logger.error("获取该时间下数据发生异常:{}",inputTime,e);
        }
        return null;
    }

    @Override
    public byte[] previewByName(String fileName) {
        try {

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

    @Override
    public List<String> getDateStrs(String inputTime) {
        try {
            if (inputTime == null || inputTime.length() < 9){
                return null;
            }
            LocalDate start = DateLocalUtils.parseMonthToStart(inputTime);
            LocalDate end = DateLocalUtils.parseMonthToEnd(inputTime);
            QueryWrapper<UNVInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",start,end);
            List<UNVInfo> infos = unvInfoMapper.selectList(queryWrapper);
            if (infos == null || infos.size() == 0){
                return null;
            }


            return infos.stream()
                    .filter(unvInfo -> unvInfo.getInputTime() != null)
                    .map(unvInfo -> {
                        return DateLocalUtils.parseDateToStrTwo(unvInfo.getInputTime());
                    })
                    .sorted()
                    .distinct()
                    .collect(Collectors.toList());


        }catch (Exception e){
            logger.error("获取给定月日期列表失败",e);
        }
        return null;
    }


    public String getPathByName(String fileName){
        try {
            if (fileName == null){
                return null;
            }
            QueryWrapper<UNVInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_name",fileName);
            queryWrapper.orderByDesc("update_time");

            List<UNVInfo> unvInfos = unvInfoMapper.selectList(queryWrapper);

            if (unvInfos != null && unvInfos.size() > 0){
                return unvInfos.get(0).getFilePath();
            }

        }catch (Exception e){
            logger.error("通过文件名获取路径出错",e);
        }
        return null;
    }









}
