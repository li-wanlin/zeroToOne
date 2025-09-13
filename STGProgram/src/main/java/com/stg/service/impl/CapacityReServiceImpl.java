package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.CapacityRe;
import com.stg.mapper.CapacityReMapper;
import com.stg.service.CapacityReService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.capacityVo.CapaResponse;
import com.stg.vo.capacityVo.CapaUnit;
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
public class CapacityReServiceImpl extends ServiceImpl<CapacityReMapper, CapacityRe> implements CapacityReService {


    @Resource
    CapacityReMapper capacityReMapper;


    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.capacityRe}")
    private String capaPDF;

    private static final Logger logger = LoggerFactory.getLogger(CapacityReServiceImpl.class);


    @Override
    public Boolean insertByInfo(CapaUnit unit, MultipartFile file) {
        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, capaPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }



            CapacityRe per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return save(per);
        }catch (Exception e){
            logger.error("插入库容复核数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(CapaUnit unit) {
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
            logger.error("删除库容复核数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(CapaUnit unit, MultipartFile file) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, capaPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            CapacityRe per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return updateById(per);
        }catch (Exception e){
            logger.error("更新库容复核数据失败",e);
        }
        return false;
    }

    @Override
    public CapaResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<CapacityRe> page = new Page<>(pageNum, pageSize);
            QueryWrapper<CapacityRe> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("ca_time",start,end);
            }

            queryWrapper.orderByAsc("update_time");

            Page<CapacityRe> reinPage = capacityReMapper.selectPage(page, queryWrapper);

            return this.pageToRes(reinPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取库容复核分页数据失败",e);
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


    public CapacityRe unitToCa(CapaUnit unit){
        try {
            if (unit == null){
                return null;
            }
            CapacityRe re = new CapacityRe();


            re.setId(unit.getId());

            if (unit.getAppUnit() != null){
                re.setAppUnit(unit.getAppUnit());
            }

            if (unit.getCaUnit() != null){
                re.setCaUnit(unit.getCaUnit());
            }

            if (unit.getProNotes() != null){
                re.setProNotes(unit.getProNotes());
            }


            if (unit.getCaTime() != null){
                re.setCaTime(DateLocalUtils.parseGiveStrToDate(unit.getCaTime()));
            }

            re.setUpdateTime(new Date());

            return re;

        }catch (Exception e){
            logger.error("转换库容复核数据失败",e);
        }
        return null;
    }



    public CapaResponse pageToRes(IPage<CapacityRe> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            CapaResponse response = new CapaResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取库容复核分页数据成功");
            response.setMeta(meta);
            List<CapaUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (CapacityRe per:page.getRecords()) {

                CapaUnit unit = new CapaUnit();

                unit.setId(per.getId());
                unit.setAppUnit(per.getAppUnit());
                unit.setCaTime(DateLocalUtils.parseDateToStrTwo(per.getCaTime()));
                unit.setCaUnit(per.getCaUnit());
                unit.setProNotes(per.getProNotes());

                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setCapaUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("获取库容复核分页数据失败",e);
        }
        return null;
    }

}
