package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.SiltationControl;
import com.stg.mapper.SiltaMapper;
import com.stg.service.SiltaService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.pdfVo.PDFGeneralInfo;
import com.stg.vo.siltaVo.SiltaResponse;
import com.stg.vo.siltaVo.SiltaUnit;
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
public class SiltaServiceImpl extends ServiceImpl<SiltaMapper, SiltationControl> implements SiltaService {

    @Resource
    SiltaMapper siltaMapper;


    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.silta}")
    private String siltaPDF;

    private static final Logger logger = LoggerFactory.getLogger(SiltaServiceImpl.class);



    @Override
    public Boolean insertByInfo(SiltaUnit unit, MultipartFile file) {
        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, siltaPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }



            SiltationControl per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return save(per);
        }catch (Exception e){
            logger.error("插入淤积治理数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(SiltaUnit unit) {
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
            logger.error("删除淤积治理数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(SiltaUnit unit, MultipartFile file) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, siltaPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            SiltationControl per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return updateById(per);
        }catch (Exception e){
            logger.error("更新淤积治理数据失败",e);
        }
        return false;
    }

    @Override
    public SiltaResponse pagedQuery(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<SiltationControl> page = new Page<>(pageNum, pageSize);
            QueryWrapper<SiltationControl> queryWrapper = new QueryWrapper<>();

            queryWrapper.orderByAsc("update_time");

            Page<SiltationControl> reinPage = siltaMapper.selectPage(page, queryWrapper);

            return this.pageToRes(reinPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取淤积治理分页数据失败",e);
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


    public SiltationControl unitToCa(SiltaUnit unit){
        try {
            if (unit == null){
                return null;
            }
            SiltationControl control = new SiltationControl();

            control.setId(unit.getId());

            if (unit.getActAmount() != null){
                control.setActAmount(unit.getActAmount());
            }

            if (unit.getActUnit() != null){
                control.setActUnit(unit.getActUnit());
            }

            if (unit.getAppUnit() != null){
                control.setAppUnit(unit.getAppUnit());
            }

            if (unit.getPlanAmount() != null){
                control.setPlanAmount(unit.getPlanAmount());
            }

            if (unit.getPlanDuration() != null){
                control.setPlanDuration(unit.getPlanDuration());
            }

            if (unit.getStTime() != null){
                control.setStTime(DateLocalUtils.parseGiveStrToDate(unit.getStTime()));
            }

            if (unit.getEndTime() != null){
                control.setEndTime(DateLocalUtils.parseGiveStrToDate(unit.getEndTime()));
            }

            control.setUpdateTime(new Date());

            return control;

        }catch (Exception e){
            logger.error("转换淤积治理数据失败",e);
        }
        return null;
    }



    public SiltaResponse pageToRes(IPage<SiltationControl> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            SiltaResponse response = new SiltaResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取淤积治理分页数据成功");
            response.setMeta(meta);
            List<SiltaUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (SiltationControl per:page.getRecords()) {

                SiltaUnit unit = new SiltaUnit();


                unit.setId(per.getId());
                unit.setActAmount(per.getActAmount());
                unit.setActUnit(per.getActUnit());
                unit.setAppUnit(per.getAppUnit());
                unit.setPlanAmount(per.getPlanAmount());
                unit.setPlanDuration(per.getPlanDuration());

                unit.setStTime(DateLocalUtils.parseDateToStrTwo(per.getStTime()));
                unit.setEndTime(DateLocalUtils.parseDateToStrTwo(per.getEndTime()));


                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setSiltaUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("获取淤积治理分页数据失败",e);
        }
        return null;
    }


}
