package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.PitfallCheck;
import com.stg.mapper.CheckMapper;
import com.stg.service.CheckService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.checkVo.CheckResponse;
import com.stg.vo.checkVo.CheckUnit;
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
public class CheckServiceImpl extends ServiceImpl<CheckMapper, PitfallCheck> implements CheckService {

    @Resource
    CheckMapper checkMapper;

    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.check}")
    private String checkPDF;

    private static final Logger logger = LoggerFactory.getLogger(CheckServiceImpl.class);




    @Override
    public Boolean insertByInfo(CheckUnit unit, MultipartFile file) {
        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, checkPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }



            PitfallCheck per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return save(per);
        }catch (Exception e){
            logger.error("插入隐患排查数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(CheckUnit unit) {
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
            logger.error("删除隐患排查数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(CheckUnit unit, MultipartFile file) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, checkPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            PitfallCheck per = this.unitToCa(unit);

            if (fileName != null && filePath != null){
                per.setFileName(fileName);
                per.setFilePath(filePath);
            }

            return updateById(per);
        }catch (Exception e){
            logger.error("更新隐患排查数据失败",e);
        }
        return false;
    }

    @Override
    public CheckResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<PitfallCheck> page = new Page<>(pageNum, pageSize);
            QueryWrapper<PitfallCheck> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("ck_time",start,end);
            }

            queryWrapper.orderByAsc("update_time");

            Page<PitfallCheck> reinPage = checkMapper.selectPage(page, queryWrapper);

            return this.pageToRes(reinPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取隐患排查分页数据失败",e);
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


    public PitfallCheck unitToCa(CheckUnit unit){
        try {
            if (unit == null){
                return null;
            }
            PitfallCheck re = new PitfallCheck();


            re.setId(unit.getId());

            if (unit.getCkContent() != null){
                re.setCkContent(unit.getCkContent());
            }

            if (unit.getCkName() != null){
                re.setCkName(unit.getCkName());
            }

            if (unit.getCkPerson() != null){
                re.setCkPerson(unit.getCkPerson());
            }

            if (unit.getCkProb() != null){
                re.setCkProb(unit.getCkProb());
            }

            if (unit.getHandleMea() != null){
                re.setHandleMea(unit.getHandleMea());
            }

            if (unit.getHandleRes() != null){
                re.setHandleRes(unit.getHandleRes());
            }

            if (unit.getProNotes() != null){
                re.setProNotes(unit.getProNotes());
            }

            if (unit.getCkTime() != null){
                re.setCkTime(DateLocalUtils.parseGiveStrToDate(unit.getCkTime()));
            }

            re.setUpdateTime(new Date());

            return re;

        }catch (Exception e){
            logger.error("转换隐患排查数据失败",e);
        }
        return null;
    }



    public CheckResponse pageToRes(IPage<PitfallCheck> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            CheckResponse response = new CheckResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取隐患排查分页数据成功");
            response.setMeta(meta);
            List<CheckUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (PitfallCheck per:page.getRecords()) {

                CheckUnit unit = new CheckUnit();

                unit.setId(per.getId());
                unit.setCkContent(per.getCkContent());
                unit.setCkName(per.getCkName());
                unit.setCkPerson(per.getCkPerson());
                unit.setCkProb(per.getCkProb());
                unit.setHandleMea(per.getHandleMea());
                unit.setHandleRes(per.getHandleRes());
                unit.setCkTime(DateLocalUtils.parseDateToStrTwo(per.getCkTime()));
                unit.setProNotes(per.getProNotes());


                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setCheckUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("获取隐患排查分页数据失败",e);
        }
        return null;
    }




}
