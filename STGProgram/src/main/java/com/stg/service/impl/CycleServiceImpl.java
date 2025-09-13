package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.DeviceCycle;
import com.stg.mapper.CycleMapper;
import com.stg.service.CycleService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.cycleVo.CycleResponse;
import com.stg.vo.cycleVo.CycleUnit;
import com.stg.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CycleServiceImpl extends ServiceImpl<CycleMapper, DeviceCycle> implements CycleService {


    @Resource
    CycleMapper cycleMapper;

    private static final Logger logger = LoggerFactory.getLogger(CycleServiceImpl.class);



    @Override
    public Boolean insertByInfo(CycleUnit unit) {
        try {
            if (unit == null){
                return false;
            }

            return save(this.unitToCy(unit));

        }catch (Exception e){
            logger.error("插入设备全周期数据发生异常",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(CycleUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            return removeById(this.unitToCy(unit));

        }catch (Exception e){
            logger.error("删除设备全周期数据发生异常",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(CycleUnit unit) {
        try {
            if (unit == null){
                return false;
            }

            return updateById(this.unitToCy(unit));

        }catch (Exception e){
            logger.error("更新设备全周期数据发生异常",e);
        }
        return false;
    }

    @Override
    public CycleResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {

            QueryWrapper<DeviceCycle> queryWrapper = new QueryWrapper<>();
            Page<DeviceCycle> page = new Page<>(pageNum, pageSize);

            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("install_time",start,end);
            }

            queryWrapper.orderByAsc("install_time");

            Page<DeviceCycle> cyclePage = cycleMapper.selectPage(page, queryWrapper);

            return this.pageToRes(cyclePage,pageNum,pageSize);

        }catch (Exception e){
            logger.error("获取设备全周期分页数据发生异常",e);
        }
        return null;
    }



    public DeviceCycle unitToCy(CycleUnit unit){
        try {
            if (unit == null){
                return null;
            }

            DeviceCycle cycle = new DeviceCycle();

            cycle.setId(unit.getId());

            if (unit.getDeviceLeader() != null){
                cycle.setDeviceLeader(unit.getDeviceLeader());
            }

            if (unit.getDeviceLo() != null){
                cycle.setDeviceLo(unit.getDeviceLo());
            }

            if (unit.getDeviceLv() != null){
                cycle.setDeviceLv(unit.getDeviceLv());
            }

            if (unit.getDeviceModel() != null){
                cycle.setDeviceModel(unit.getDeviceModel());
            }

            if (unit.getDeviceName() != null){
                cycle.setDeviceName(unit.getDeviceName());
            }

            if (unit.getProNotes() != null){
                cycle.setProNotes(unit.getProNotes());
            }

            if (unit.getInstallTime() != null){
                cycle.setInstallTime(DateLocalUtils.parseGiveStrToDate(unit.getInstallTime()));
            }

            if (unit.getScrapTime() != null){
                cycle.setScrapTime(DateLocalUtils.parseGiveStrToDate(unit.getScrapTime()));
            }

            cycle.setUpdateTime(new Date());

            return cycle;

        }catch (Exception e){
            logger.error("设备全周期数据转换发生异常",e);
        }
        return null;
    }


    public CycleResponse pageToRes(IPage<DeviceCycle> page,Integer pageNum,Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            CycleResponse response = new CycleResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取设备全周期分页数据成功");
            response.setMeta(meta);
            List<CycleUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1 ;


            for (DeviceCycle cy:page.getRecords()) {
                CycleUnit unit = new CycleUnit();

                unit.setId(cy.getId());
                unit.setOrderNum(startIndex++);

                unit.setDeviceLeader(cy.getDeviceLeader());
                unit.setDeviceLo(cy.getDeviceLo());
                unit.setDeviceLv(cy.getDeviceLv());
                unit.setDeviceModel(cy.getDeviceModel());
                unit.setDeviceName(cy.getDeviceName());
                unit.setProNotes(cy.getProNotes());
                unit.setInstallTime(DateLocalUtils.parseDateToStrTwo(cy.getInstallTime()));
                unit.setScrapTime(DateLocalUtils.parseDateToStrTwo(cy.getScrapTime()));

                units.add(unit);
            }

            response.setCycleUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;

        }catch (Exception e){
            logger.error("获取设备全周期分页数据发生异常",e);
        }
        return null;
    }



}
