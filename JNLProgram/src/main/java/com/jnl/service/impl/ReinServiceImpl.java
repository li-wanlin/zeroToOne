package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.Reinforcement;
import com.jnl.mapper.ReinforcementMapper;
import com.jnl.service.ReinService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.pdfVo.PDFGeneralInfo;
import com.jnl.vo.reinVo.ReinResponse;
import com.jnl.vo.reinVo.ReinUnit;
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
public class ReinServiceImpl extends ServiceImpl<ReinforcementMapper, Reinforcement> implements ReinService {

    @Resource
    ReinforcementMapper reinforcementMapper;

    @Resource
    PDFInfoServiceImpl pdfInfoService;

    @Value("${PDF.upload.reinforcement}")
    private String reinPDF;

    private static final Logger logger = LoggerFactory.getLogger(ReinServiceImpl.class);



    @Override
    public Boolean insertByInfo(ReinUnit unit, MultipartFile file) {
        try {
            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, reinPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            Reinforcement re = this.unitToRe(unit);

            if (fileName != null && filePath != null){
                re.setFileName(fileName);
                re.setFilePath(filePath);
            }

            return save(re);
        }catch (Exception e){
            logger.error("插入除险加固数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(ReinUnit unit) {
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
            logger.error("删除除险加固数据失败",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(ReinUnit unit, MultipartFile file) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            String fileName = null;
            String filePath = null;

            if (file != null){
                PDFGeneralInfo generalInfo = pdfInfoService.generalPDFUp(file, reinPDF);
                if (generalInfo != null && generalInfo.getIfCom()){
                    fileName = generalInfo.getFileName();
                    filePath = generalInfo.getFilePath();
                }
            }

            Reinforcement re = this.unitToRe(unit);

            if (fileName != null && filePath != null){
                re.setFileName(fileName);
                re.setFilePath(filePath);
            }

            return updateById(re);
        }catch (Exception e){
            logger.error("更新除险加固数据失败",e);
        }
        return false;
    }

    @Override
    public ReinResponse pagedQuery(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<Reinforcement> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Reinforcement> queryWrapper = new QueryWrapper<>();

            queryWrapper.orderByAsc("update_time");

            Page<Reinforcement> reinPage = reinforcementMapper.selectPage(page, queryWrapper);

            return this.pageToRes(reinPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取除险加固分页数据失败",e);
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



    public Reinforcement unitToRe(ReinUnit unit){
        try {
            if (unit == null){
                return null;
            }
            Reinforcement re = new Reinforcement();

            re.setId(unit.getId());
            if (unit.getActUnit() != null){
                re.setActUnit(unit.getActUnit());
            }

            if (unit.getAppUnit() != null){
                re.setAppUnit(unit.getAppUnit());
            }

            if (unit.getDesignUnit() != null){
                re.setDesignUnit(unit.getDesignUnit());
            }

            if (unit.getManUnit() != null){
                re.setManUnit(unit.getManUnit());
            }


            if (unit.getProContent() != null){
                re.setProContent(unit.getProContent());
            }

            if (unit.getProName() != null){
                re.setProName(unit.getProName());
            }

            if (unit.getProNotes() != null){
                re.setProNotes(unit.getProNotes());
            }

            if (unit.getStartTime() != null){
                re.setStartTime(DateLocalUtils.parseGiveStrToDate(unit.getStartTime()));
            }

            if (unit.getPlanTime() != null){
                re.setPlanTime(DateLocalUtils.parseGiveStrToDate(unit.getPlanTime()));
            }

            if (unit.getActUnit() != null){
                re.setFinishTime(DateLocalUtils.parseGiveStrToDate(unit.getFinishTime()));
            }


            re.setUpdateTime(new Date());

            return re;

        }catch (Exception e){
            logger.error("转换除险加固数据失败",e);
        }
        return null;
    }


    public ReinResponse pageToRes(IPage<Reinforcement> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            ReinResponse response = new ReinResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取除险加固分页数据成功");
            response.setMeta(meta);
            List<ReinUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (Reinforcement re:page.getRecords()) {

                ReinUnit unit = new ReinUnit();


                unit.setId(re.getId());
                unit.setActUnit(re.getActUnit());
                unit.setAppUnit(re.getAppUnit());
                unit.setDesignUnit(re.getDesignUnit());
                unit.setManUnit(re.getManUnit());
                unit.setProContent(re.getProContent());
                unit.setProName(re.getProName());
                unit.setProNotes(re.getProNotes());
                unit.setStartTime(DateLocalUtils.parseDateToStrTwo(re.getStartTime()));
                unit.setPlanTime(DateLocalUtils.parseDateToStrTwo(re.getPlanTime()));
                unit.setFinishTime(DateLocalUtils.parseDateToStrTwo(re.getFinishTime()));

                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setReinUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("获取除险加固分页数据失败",e);
        }
        return null;
    }




}
