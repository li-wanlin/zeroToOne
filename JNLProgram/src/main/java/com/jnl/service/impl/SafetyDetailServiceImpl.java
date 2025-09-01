package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.SafetyDetail;
import com.jnl.mapper.SafetyDetailMapper;
import com.jnl.service.SafetyDetailService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.pdfVo.PDFGeneralInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
public class SafetyDetailServiceImpl extends ServiceImpl<SafetyDetailMapper, SafetyDetail> implements SafetyDetailService {

    @Resource
    SafetyDetailMapper safetyDetailMapper;

    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.safetyDetail}")
    private String safetyPDF;


    private static final Logger logger = LoggerFactory.getLogger(SafetyDetailServiceImpl.class);


    @Override
    public Boolean insertByInfo(String project, String time, String orgUnit, String bearUnit, String conclusion, MultipartFile file) {

        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, safetyPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            SafetyDetail safetyDetail = new SafetyDetail();
            if (project != null){
                safetyDetail.setProject(project);
            }

            if (time != null){
                safetyDetail.setTime(DateLocalUtils.parseStrToDate(time));
            }

            if (orgUnit != null){
                safetyDetail.setOrgUnit(orgUnit);
            }

            if (bearUnit != null){
                safetyDetail.setBearUnit(bearUnit);
            }

            if (conclusion != null){
                safetyDetail.setConclusion(conclusion);
            }

            if (fileName != null && filePath != null){
                safetyDetail.setFileName(fileName);
                safetyDetail.setFilePath(filePath);
            }

            safetyDetail.setUpdateTime(new Date());
            return save(safetyDetail);


        }catch (Exception e){
            logger.error("安全鉴定详情插入数据发生异常",e);
        }
        return false;
    }


    @Override
    public Boolean deleteByInfo(Integer id) {
        try {
            if (id == null || id <= 0){
                return false;
            }

            String filePath = this.getFilePathById(id);
            if (filePath == null){
                return removeById(id);
            }
            Path path = Paths.get(filePath);

            if (!Files.exists(path)){
                logger.info("文件不存在");
                return removeById(id);
            }

            if (Files.isDirectory(path)){
                logger.info("拒绝删除目录");
                return removeById(id);
            }

            Files.delete(path);
            return removeById(id);

        }catch (Exception e){
            logger.error("删除安全鉴定详情文件时发生异常",e);
        }

        return false;
    }


    @Override
    public Boolean updateByInfo(Integer id, String project, String time, String orgUnit, String bearUnit, String conclusion, MultipartFile file) {
        try {
            if (id == null || id <= 0){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, safetyPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            SafetyDetail safetyDetail = new SafetyDetail();
            safetyDetail.setId(id);
            if (project != null){
                safetyDetail.setProject(project);
            }

            if (time != null){
                safetyDetail.setTime(DateLocalUtils.parseStrToDate(time));
            }

            if (orgUnit != null){
                safetyDetail.setOrgUnit(orgUnit);
            }

            if (bearUnit != null){
                safetyDetail.setBearUnit(bearUnit);
            }

            if (conclusion != null){
                safetyDetail.setConclusion(conclusion);
            }

            if (fileName != null && filePath != null){
                safetyDetail.setFileName(fileName);
                safetyDetail.setFilePath(filePath);
            }
            safetyDetail.setUpdateTime(new Date());

            return updateById(safetyDetail);


        }catch (Exception e){
            logger.error("更新安全鉴定详情数据时发生错误",e);
        }

        return false;
    }

    @Override
    public IPage<SafetyDetail> pageQuery(Integer pageNum,Integer pageSize) {
        try {

            if (pageNum == null || pageNum <= 0 || pageSize == null || pageSize <= 0){
                return null;
            }

            Page<SafetyDetail> page = new Page<>(pageNum, pageSize);
            QueryWrapper<SafetyDetail> queryWrapper = new QueryWrapper<>();

            return safetyDetailMapper.selectPage(page,queryWrapper);


        }catch (Exception e){
            logger.error("获取安全鉴定详情分页数据失败",e);
        }
        return null;
    }



    @Override
    public byte[] previewById(Integer id, long startByte) {
        try {

            String filePath = this.getFilePathById(id);
            if (filePath == null){
                return null;
            }

            return pdfInfoService.generalPreview(filePath,startByte);

        }catch (Exception e){
            logger.error("在获取服务器文件时发生异常",e);
        }
        return null;
    }


    public String getFilePathById(Integer id){
        SafetyDetail byId = getById(id);
        return byId.getFilePath();
    }


    public String getFileNameById(Integer id){
        SafetyDetail byId = getById(id);
        return byId.getFileName();
    }





}
