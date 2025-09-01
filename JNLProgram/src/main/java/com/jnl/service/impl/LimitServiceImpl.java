package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.PerDelimit;
import com.jnl.mapper.LimitMapper;
import com.jnl.service.LimitService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.pdfVo.PDFGeneralInfo;
import com.jnl.vo.perDelimitVo.LimitResponse;
import com.jnl.vo.perDelimitVo.LimitUnit;
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
public class LimitServiceImpl extends ServiceImpl<LimitMapper, PerDelimit> implements LimitService {

    @Resource
    LimitMapper limitMapper;


    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.delimit}")
    private String limitPDF;

    private static final Logger logger = LoggerFactory.getLogger(LimitServiceImpl.class);








    @Override
    public Boolean insertByInfo(LimitUnit unit, MultipartFile file) {
        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, limitPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            PerDelimit per = this.unitToPer(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return save(per);
        }catch (Exception e){
            logger.error("插入确权划界数据失败",e);
        }
        return false;
    }


    @Override
    public Boolean deleteByInfo(LimitUnit unit) {
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
            logger.error("删除确权划界数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(LimitUnit unit, MultipartFile file) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, limitPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            PerDelimit per = this.unitToPer(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return updateById(per);
        }catch (Exception e){
            logger.error("更新确权划界数据失败",e);
        }
        return false;
    }

    @Override
    public LimitResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<PerDelimit> page = new Page<>(pageNum, pageSize);
            QueryWrapper<PerDelimit> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("per_time",start,end);
            }

            queryWrapper.orderByAsc("update_time");

            Page<PerDelimit> reinPage = limitMapper.selectPage(page, queryWrapper);

            return this.pageToRes(reinPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取确权划界分页数据失败",e);
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



    public PerDelimit unitToPer(LimitUnit unit){
        try {
            if (unit == null){
                return null;
            }
            PerDelimit per = new PerDelimit();

            per.setId(unit.getId());


            if (unit.getAppUnit() != null){
                per.setAppUnit(unit.getAppUnit());
            }

            if (unit.getPerUnit() != null){
                per.setPerUnit(unit.getPerUnit());
            }

            if (unit.getProNotes() != null){
                per.setProNotes(unit.getProNotes());
            }


            if (unit.getPerTime() != null){
                per.setPerTime(DateLocalUtils.parseGiveStrToDate(unit.getPerTime()));
            }

            per.setUpdateTime(new Date());

            return per;

        }catch (Exception e){
            logger.error("转换确权划界数据失败",e);
        }
        return null;
    }



    public LimitResponse pageToRes(IPage<PerDelimit> page,Integer pageNum,Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            LimitResponse response = new LimitResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取确权划界分页数据成功");
            response.setMeta(meta);
            List<LimitUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (PerDelimit per:page.getRecords()) {

                LimitUnit unit = new LimitUnit();

                unit.setId(per.getId());
                unit.setAppUnit(per.getAppUnit());
                unit.setPerTime(DateLocalUtils.parseDateToStrTwo(per.getPerTime()));
                unit.setPerUnit(per.getPerUnit());
                unit.setProNotes(per.getProNotes());

                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setLimitUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("获取确权划界分页数据失败",e);
        }
        return null;
    }
}
