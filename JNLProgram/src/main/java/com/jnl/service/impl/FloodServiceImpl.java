package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FloodPrevent;
import com.jnl.mapper.FloodMapper;
import com.jnl.service.FloodService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.floodVo.FloodResponse;
import com.jnl.vo.floodVo.FloodUnit;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FloodServiceImpl extends ServiceImpl<FloodMapper, FloodPrevent> implements FloodService {

    @Resource
    FloodMapper floodMapper;


    @Resource
    FpImgServiceImpl fpImgService;


    private static final Logger logger = LoggerFactory.getLogger(FloodServiceImpl.class);



    @Override
    public List<FloodUnit> selectLatestTen() {
        try {
            QueryWrapper<FloodPrevent> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 10");

            List<FloodPrevent> floodPrevents = floodMapper.selectList(queryWrapper);
            if (floodPrevents == null || floodPrevents.size() == 0){
                return null;
            }

            ArrayList<FloodUnit> units = new ArrayList<>();
            for (FloodPrevent fl: floodPrevents) {
                FloodUnit unit = this.flToUnit(fl);
                units.add(unit);
            }

            return units;

        }catch (Exception e){
            logger.error("获取防汛保障数据发生异常",e);
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByInfo(FloodUnit unit, MultipartFile[] files) {
        try {
            if (unit == null || unit.getFpTime() == null){
                return false;
            }

            FloodPrevent floodPrevent = this.unitToFl(unit);

            boolean save = save(floodPrevent);
            if (save && files != null){
                fpImgService.insertByInfo(floodPrevent.getId(),files);
            }


            return save;

        }catch (Exception e){
            logger.error("插入防汛保障数据发生异常",e);
            throw new RuntimeException("数据插入失败", e);
        }
    }





    @Override
    public Boolean deleteByInfo(FloodUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return null;
            }

            boolean remove = removeById(unit.getId());

            if (remove){
                fpImgService.deleteByImgId(unit.getId());
            }


            return remove;


        }catch (Exception e){
            logger.error("删除防汛保障数据发生异常",e);
        }

        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByInfo(FloodUnit unit,MultipartFile[] files) {
        try {
            if (unit == null){
                return false;
            }

            FloodPrevent floodPrevent = this.unitToFl(unit);
            boolean update = updateById(floodPrevent);

            if (update && files != null){
                fpImgService.deleteByImgId(floodPrevent.getId());
                fpImgService.insertByInfo(floodPrevent.getId(),files);
            }


            return update;

        }catch (Exception e){
            logger.error("更新防汛保障数据发生异常",e);
            throw new RuntimeException("数据更新失败", e);
        }
    }


    @Override
    public FloodResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {

            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<FloodPrevent> page = new Page<>(pageNum, pageSize);
            QueryWrapper<FloodPrevent> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("fp_time",start,end);
            }

            queryWrapper.orderByAsc("fp_time");

            Page<FloodPrevent> repairPage = floodMapper.selectPage(page, queryWrapper);

            return this.pageToRes(repairPage,pageNum,pageSize);

        }catch (Exception e){
            logger.error("获取分页数据发生异常",e);
        }
        return null;
    }



    @Override
    public List<String> getFileNames(Integer imgId) {
        try {
            if (imgId == null){
                return null;
            }

            return fpImgService.getFileNames(imgId);


        }catch (Exception e){
            logger.error("",e);
        }

        return null;
    }



    @Override
    public byte[] previewByName(String fileName) {
        return fpImgService.previewByName(fileName);
    }






    public FloodUnit flToUnit(FloodPrevent fl){
        try {
            if (fl == null){
                return null;
            }

            FloodUnit unit = new FloodUnit();
            unit.setId(fl.getId());
            unit.setFpTime(DateLocalUtils.parseDateToStrTwo(fl.getFpTime()));
            unit.setFpLocation(fl.getFpLocation());
            unit.setFpOutbound(fl.getFpOutbound());
            unit.setFpStock(fl.getFpStock());
            unit.setFpStore(fl.getFpStore());
            unit.setFpType(fl.getFpType());
            unit.setFpUnit(fl.getFpUnit());
            unit.setOutOperator(fl.getOutOperator());
            unit.setProNotes(fl.getProNotes());
            unit.setStoreOperator(fl.getStoreOperator());

            return unit;
        }catch (Exception e){
            logger.error("防汛保障数据转换失败",e);
        }
        return null;
    }



    public FloodPrevent unitToFl(FloodUnit unit){
        try {
            if (unit == null){
                return null;
            }


            FloodPrevent fl = new FloodPrevent();
            fl.setId(unit.getId());

            if (unit.getFpTime()!= null){
                fl.setFpTime(DateLocalUtils.parseGiveStrToDate(unit.getFpTime()));
            }


            if (unit.getFpLocation() != null){
                fl.setFpLocation(unit.getFpLocation());
            }


            if (unit.getFpOutbound() != null){
                fl.setFpOutbound(unit.getFpOutbound());
            }

            if (unit.getFpStock() != null){
                fl.setFpStock(unit.getFpStock());
            }

            if (unit.getFpStore() != null){
                fl.setFpStore(unit.getFpStore());
            }

            if (unit.getFpType() != null){
                fl.setFpType(unit.getFpType());
            }

            if (unit.getFpUnit() != null){
                fl.setFpUnit(unit.getFpUnit());
            }

            if (unit.getOutOperator() != null){
                fl.setOutOperator(unit.getOutOperator());
            }

            if (unit.getProNotes() != null){
                fl.setProNotes(unit.getProNotes());
            }

            if (unit.getStoreOperator() != null){
                fl.setStoreOperator(unit.getStoreOperator());
            }

            fl.setUpdateTime(new Date());
            return fl;

        }catch (Exception e){
            logger.error("防汛保障数据转换失败",e);
        }
        return null;
    }


    public FloodResponse pageToRes(IPage<FloodPrevent> page,Integer pageNum,Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            FloodResponse response = new FloodResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取防汛保障分页数据成功");
            response.setMeta(meta);
            List<FloodUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;
            for (FloodPrevent fl:page.getRecords()) {
                FloodUnit unit = this.flToUnit(fl);
                unit.setOrderNum(startIndex++);

                units.add(unit);
            }
            response.setFloodUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("防汛保障分页数据转换出错",e);
        }
        return null;
    }




}
