package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.GNSSData;
import com.jnl.entity.SafetyWarn;
import com.jnl.entity.SeepageInfo;
import com.jnl.mapper.SafetyWarnMapper;
import com.jnl.service.SafetyWarnService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.fourPreventVo.WarnResponse;
import com.jnl.vo.fourPreventVo.WarnUnit;
import com.jnl.vo.functionVo.GlobalOnenetTime;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class SafetyWarnServiceImpl extends ServiceImpl<SafetyWarnMapper, SafetyWarn> implements SafetyWarnService {

    @Resource
    SafetyWarnMapper safetyWarnMapper;

    @Resource
    GlobalOnenetTime globalOnenetTime;

    private static final Logger logger = LoggerFactory.getLogger(SafetyWarnServiceImpl.class);


    @Override
    @Async("asyncExecutor")
    public void saveGNSS(GNSSData gnss) {
        try {
            if (gnss == null || gnss.getDeviceName() == null || gnss.getTime() == null){
                return;
            }

            Map<String, String> map = new HashMap<>();
            map.put("牛岭水库M1","位移点1");
            map.put("牛岭水库M2","位移点2");
            map.put("牛岭水库M3","位移点3");
            map.put("X","ΔX");
            map.put("Y","ΔY");
            map.put("H","ΔH");


            List<SafetyWarn> safetyWarns = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.0");

            Double planeX = gnss.getTargetVariationPlaneX();
            Double planeY = gnss.getTargetVariationPlaneY();
            Double planeH = gnss.getTargetVariationPlaneH();

            if (planeX != null && Math.abs(planeX) >= 15){
                SafetyWarn safetyWarn = new SafetyWarn();
                safetyWarn.setDevice(map.get(gnss.getDeviceName()));
                safetyWarn.setMonValue(gnss.getTargetVariationPlaneX());
                safetyWarn.setUpdateTime(gnss.getTime());
                safetyWarn.setLimitType(map.get("X"));
                safetyWarn.setLimitState("+"+df.format(Math.abs(planeX)-15)+"mm");
                safetyWarns.add(safetyWarn);
            }

            if (planeY != null && Math.abs(planeY) >= 15){
                SafetyWarn safetyWarn = new SafetyWarn();
                safetyWarn.setDevice(map.get(gnss.getDeviceName()));
                safetyWarn.setMonValue(gnss.getTargetVariationPlaneY());
                safetyWarn.setUpdateTime(gnss.getTime());
                safetyWarn.setLimitType(map.get("Y"));
                safetyWarn.setLimitState("+"+df.format(Math.abs(planeY)-15)+"mm");
                safetyWarns.add(safetyWarn);
            }

            if (planeH != null && Math.abs(planeH) >= 15){
                SafetyWarn safetyWarn = new SafetyWarn();
                safetyWarn.setDevice(map.get(gnss.getDeviceName()));
                safetyWarn.setMonValue(gnss.getTargetVariationPlaneH());
                safetyWarn.setUpdateTime(gnss.getTime());
                safetyWarn.setLimitType(map.get("H"));
                safetyWarn.setLimitState("+"+df.format(Math.abs(planeH)-15)+"mm");
                safetyWarns.add(safetyWarn);
            }

            if (safetyWarns.size() == 0){
                return;
            }

            saveBatch(safetyWarns);

        }catch (Exception e){
            logger.error("计算预警安全数据发生异常",e);
        }

    }

    @Override
    @Async("asyncExecutor")
    public void saveOnenet(SeepageInfo info) {
        try {
            if (info.getUpdateTime() == null || (info.getLv() == null && info.getLv2() == null && info.getLv3() == null)){
                return;
            }
            double limit = 20d;
            double HSGLimit = 20d;
            Double lv = info.getLv();
            Double lv2 = info.getLv2();
            Double lv3 = info.getLv3();



            List<SafetyWarn> safetyWarns = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.0");

            Long deviceSevenOne = globalOnenetTime.getDeviceSevenOne() == null ? 0 : globalOnenetTime.getDeviceSevenOne();
            Long deviceSevenTwo = globalOnenetTime.getDeviceSevenTwo() == null ? 0 : globalOnenetTime.getDeviceSevenTwo();
            Long deviceSevenThree = globalOnenetTime.getDeviceSevenThree() == null ? 0 : globalOnenetTime.getDeviceSevenThree();


            //利用日期判断汇水沟限制
            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = LocalDate.of(currentDate.getYear(), Month.MAY, 15);
            LocalDate endDate = LocalDate.of(currentDate.getYear(), Month.OCTOBER, 2);

            boolean isInRange = !currentDate.isBefore(startDate) && currentDate.isBefore(endDate);
            if (isInRange){
                HSGLimit = 50d;
            }


            if (lv != null && info.getLv() > limit && DateLocalUtils.parseDateToLong(info.getUpdateTime()) > (deviceSevenOne+1000*60*10)){
                SafetyWarn safetyWarn = new SafetyWarn();
                safetyWarn.setUpdateTime(info.getUpdateTime());
                safetyWarn.setMonValue(info.getLv());
                safetyWarn.setDevice("集小井");
                safetyWarn.setLimitType("渗流量");
                safetyWarn.setLimitState("+"+df.format(info.getLv()-limit)+"L/s");

                globalOnenetTime.setDeviceSevenOne(DateLocalUtils.parseDateToLong(info.getUpdateTime()));
                safetyWarns.add(safetyWarn);
            }

            if (lv2 != null && info.getLv2() > limit && DateLocalUtils.parseDateToLong(info.getUpdateTime()) > (deviceSevenTwo+1000*60*10)){
                SafetyWarn safetyWarn = new SafetyWarn();
                safetyWarn.setUpdateTime(info.getUpdateTime());
                safetyWarn.setMonValue(info.getLv2());
                safetyWarn.setDevice("量水堰");
                safetyWarn.setLimitType("渗流量");
                safetyWarn.setLimitState("+"+df.format(info.getLv2()-limit)+"L/s");

                globalOnenetTime.setDeviceSevenTwo(DateLocalUtils.parseDateToLong(info.getUpdateTime()));
                safetyWarns.add(safetyWarn);
            }

            if (lv3 != null && info.getLv3() > HSGLimit && DateLocalUtils.parseDateToLong(info.getUpdateTime()) > (deviceSevenThree+1000*60*10)){
                SafetyWarn safetyWarn = new SafetyWarn();
                safetyWarn.setUpdateTime(info.getUpdateTime());
                safetyWarn.setMonValue(info.getLv3());
                safetyWarn.setDevice("汇水沟");
                safetyWarn.setLimitType("渗流量");
                safetyWarn.setLimitState("+"+df.format(info.getLv3()-HSGLimit)+"L/s");

                globalOnenetTime.setDeviceSevenThree(DateLocalUtils.parseDateToLong(info.getUpdateTime()));
                safetyWarns.add(safetyWarn);
            }

            if (safetyWarns.size() > 0){
                saveBatch(safetyWarns);
            }


        }catch (Exception e){
            logger.error("计算预警安全数据发生异常",e);
        }
    }

    @Override
    public WarnResponse selectLatestOver() {
        WarnResponse response = new WarnResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {

            QueryWrapper<SafetyWarn> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 20");
            List<SafetyWarn> limits = safetyWarnMapper.selectList(queryWrapper);
            if (limits == null || limits.size() == 0){
                meta.setStatus(400);
                meta.setMsg("获取预警安全数据成功");
                return response;
            }

            List<WarnUnit> units = new ArrayList<>();
            for (SafetyWarn ol:limits) {
                WarnUnit unit = this.limitToUnit(ol);
                units.add(unit);
            }
            meta.setStatus(200);
            meta.setMsg("获取预警安全数据成功");
            response.setUnits(units);
            return response;


        }catch (Exception e){
            logger.error("获取预警安全数据发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取预警安全数据失败");
        return response;
    }

    @Override
    public WarnResponse pagedQueryOver(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<SafetyWarn> page = new Page<>(pageNum, pageSize);
            QueryWrapper<SafetyWarn> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("update_time",start,end);
            }
            queryWrapper.orderByAsc("update_time");
            Page<SafetyWarn> limitPage = safetyWarnMapper.selectPage(page, queryWrapper);
            return this.pageToRes(limitPage,pageNum,pageSize);

        }catch (Exception e){
            logger.error("获取预警安全分页数据发生异常",e);
        }
        return null;
    }



    public WarnUnit limitToUnit(SafetyWarn ol){
        try {

            if (ol == null){
                return null;
            }


            WarnUnit unit = new WarnUnit();
            unit.setUpdateTime(DateLocalUtils.parseDateToStr(ol.getUpdateTime()));
            unit.setLocation(ol.getDevice());
            unit.setDevice(ol.getDevice());
            unit.setMonValue(ol.getMonValue());
            unit.setLimitType(ol.getLimitType());
            unit.setLimitState(ol.getLimitState());


            return unit;
        }catch (Exception e){
            logger.error("预警安全转换发生异常",e);
        }
        return null;
    }


    public WarnResponse pageToRes(IPage<SafetyWarn> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }


            WarnResponse response = new WarnResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取预警安全数据成功");
            response.setMeta(meta);
            List<WarnUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;

            for (SafetyWarn ol:page.getRecords()) {

                WarnUnit unit = limitToUnit(ol);

                unit.setOrderNum(startIndex++);

                units.add(unit);
            }

            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            response.setUnits(units);
            return response;

        }catch (Exception e){
            logger.error("预警安全转换发生异常",e);
        }
        return null;
    }



}
