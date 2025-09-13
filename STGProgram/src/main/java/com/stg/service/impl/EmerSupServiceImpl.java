package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.EmerSup;
import com.stg.mapper.EmerSupMapper;
import com.stg.service.EmerSupService;
import com.stg.vo.emerVo.EmerResponse;
import com.stg.vo.emerVo.EmerUnit;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.pdfVo.PDFGeneralInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EmerSupServiceImpl extends ServiceImpl<EmerSupMapper, EmerSup> implements EmerSupService {


    @Resource
    EmerSupMapper emerSupMapper;


    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.emerSup}")
    private String supPDF;

    private static final Logger logger = LoggerFactory.getLogger(EmerSupServiceImpl.class);



    @Override
    public List<EmerUnit> selectLatestTen() {
        try {
            QueryWrapper<EmerSup> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("sup_year");
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 10");

            List<EmerSup> floodPrevents = emerSupMapper.selectList(queryWrapper);
            if (floodPrevents == null || floodPrevents.size() == 0){
                return null;
            }

            ArrayList<EmerUnit> units = new ArrayList<>();
            for (EmerSup fl: floodPrevents) {

                EmerUnit unit = this.emToUnit(fl);
                units.add(unit);
            }

            return units;

        }catch (Exception e){
            logger.error("获取防汛保障数据发生异常",e);
        }

        return null;
    }

    @Override
    public Boolean insertByInfo(EmerUnit unit, MultipartFile file) {
        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, supPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }



            EmerSup per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return save(per);
        }catch (Exception e){
            logger.error("插入应急保障数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(EmerUnit unit) {

        try {

            if (unit == null || unit.getId() == null){
                return false;
            }



            String filePath = this.getFilePathById(unit.getId());
            if (filePath == null){
                return removeById(unit.getId());
            }
            Path path = Paths.get(filePath);

            if (!Files.exists(path)){
                logger.info("文件不存在");
                return removeById(unit.getId());
            }

            if (Files.isDirectory(path)){
                logger.info("拒绝删除目录");
                return removeById(unit.getId());
            }

            Files.delete(path);
            return removeById(unit.getId());
        }catch (Exception e){
            logger.error("删除应急保障数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(EmerUnit unit, MultipartFile file) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, supPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            EmerSup per = this.unitToCa(unit);


            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return updateById(per);
        }catch (Exception e){
            logger.error("更新应急保障数据失败",e);
        }
        return false;
    }

    @Override
    public EmerResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<EmerSup> page = new Page<>(pageNum, pageSize);
            QueryWrapper<EmerSup> queryWrapper = new QueryWrapper<>();
            if (year != null){
                queryWrapper.eq("sup_year",year);
            }

            queryWrapper.orderByAsc("update_time");

            Page<EmerSup> reinPage = emerSupMapper.selectPage(page, queryWrapper);

            return this.pageToRes(reinPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取应急保障分页数据失败",e);
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
        return getById(id).getFilePath();
    }


    public String getFileNameById(Integer id){
        return getById(id).getFileName();
    }


    public EmerSup unitToCa(EmerUnit unit){
        try {
            if (unit == null){
                return null;
            }
            EmerSup re = new EmerSup();


            re.setId(unit.getId());

            if (unit.getAppUnit() != null){
                re.setAppUnit(unit.getAppUnit());
            }

            if (unit.getSupType() != null){
                re.setSupType(unit.getSupType());
            }

            if (unit.getProNotes() != null){
                re.setProNotes(unit.getProNotes());
            }

            if (unit.getSupYear() != null){
                re.setSupYear(unit.getSupYear());
            }


            if (unit.getSupState() != null){
                re.setSupState(unit.getSupState());
            }


            re.setUpdateTime(new Date());

            return re;

        }catch (Exception e){
            logger.error("转换应急保障数据失败",e);
        }
        return null;
    }



    public EmerResponse pageToRes(IPage<EmerSup> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            EmerResponse response = new EmerResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取应急保障分页数据成功");
            response.setMeta(meta);
            List<EmerUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (EmerSup per:page.getRecords()) {

                EmerUnit unit = new EmerUnit();

                unit.setId(per.getId());
                unit.setAppUnit(per.getAppUnit());
                unit.setSupType(per.getSupType());
                unit.setSupYear(per.getSupYear());
                unit.setSupState(per.getSupState());
                unit.setProNotes(per.getProNotes());

                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setEmerUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("获取应急保障分页数据失败",e);
        }
        return null;
    }




    public EmerUnit emToUnit(EmerSup su){
        if (su == null){
            return null;
        }

        EmerUnit unit = new EmerUnit();
        unit.setId(su.getId());
        unit.setAppUnit(su.getAppUnit());
        unit.setSupType(su.getSupType());
        unit.setSupYear(su.getSupYear());
        unit.setSupState(su.getSupState());
        unit.setProNotes(su.getProNotes());
        return unit;
    }



}
