package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.UNSInfo;
import com.jnl.mapper.UNSInfoMapper;
import com.jnl.service.UNSInfoService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.imageInfoVo.ImageGeneralInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UNSInfoServiceImpl extends ServiceImpl<UNSInfoMapper, UNSInfo> implements UNSInfoService {


    @Resource
    UNSInfoMapper unsInfoMapper;


    @Resource
    ImagesInfoServiceImpl imagesInfoService;

    @Value("${image.upload.uns}")
    private String unsImage;


    private static final Logger logger = LoggerFactory.getLogger(UNSInfoServiceImpl.class);



    @Override
    public Boolean insertByInfo(String inputTime, MultipartFile[] files) {
        try {
            if (inputTime == null || files == null){
                return false;
            }

            List<UNSInfo> infos = new ArrayList<>();

            for (MultipartFile file:files) {
                ImageGeneralInfo generalInfo = imagesInfoService.generalUpload(file, unsImage);
                if (!generalInfo.getIfCom()){
                    continue;
                }
                UNSInfo info = new UNSInfo();
                info.setInputTime(DateLocalUtils.parseGiveStrToDate(inputTime));
                info.setFileName(generalInfo.getFileName());
                info.setFilePath(generalInfo.getFilePath());
                info.setUpdateTime(new Date());
                infos.add(info);
            }

            return saveBatch(infos);
        }catch (Exception e){
            logger.error("插入无人船巡查数据发生异常",e);
        }
        return false;
    }


    @Override
    public List<String> getFileNames(String inputTime) {
        try {
            QueryWrapper<UNSInfo> queryWrapper = new QueryWrapper<>();
            Date strToDate = DateLocalUtils.parseGiveStrToDate(inputTime);
            queryWrapper.eq("input_time",strToDate);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<UNSInfo> unvInfos = unsInfoMapper.selectList(queryWrapper);
            if (unvInfos == null || unvInfos.size() == 0){
                return null;
            }

            return unvInfos.stream().map(UNSInfo::getFileName).collect(Collectors.toList());
        }catch (Exception e){
            logger.error("获取该时间下最后5条数据发生异常:{}",inputTime,e);
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
            QueryWrapper<UNSInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",start,end);
            List<UNSInfo> infos = unsInfoMapper.selectList(queryWrapper);
            if (infos == null || infos.size() == 0){
                return null;
            }


            return infos.stream()
                    .filter(unsInfo -> unsInfo.getInputTime() != null)
                    .map(unsInfo -> {
                        return DateLocalUtils.parseDateToStrTwo(unsInfo.getInputTime());
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
            QueryWrapper<UNSInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_name",fileName);
            queryWrapper.orderByDesc("update_time");

            List<UNSInfo> unvInfos = unsInfoMapper.selectList(queryWrapper);

            if (unvInfos != null && unvInfos.size() > 0){
                return unvInfos.get(0).getFilePath();
            }

        }catch (Exception e){
            logger.error("通过文件名获取路径出错",e);
        }
        return null;
    }

}
