package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.*;
import com.jnl.mapper.OverLimitMapper;
import com.jnl.service.OverLimitService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.fourPreventVo.OverLimitUnit;
import com.jnl.vo.fourPreventVo.OverResponse;
import com.jnl.vo.functionVo.GlobalOnenetTime;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class OverLimitServiceImpl extends ServiceImpl<OverLimitMapper, OverLimit> implements OverLimitService {

    @Resource
    OverLimitMapper overLimitMapper;

    @Resource
    GlobalOnenetTime globalOnenetTime;


    private static final Logger logger = LoggerFactory.getLogger(OverLimitServiceImpl.class);



    @Override
    @Async("asyncExecutor")
    public void saveDeviceFour(List<OnenetDev4> list) {
        try {
            if (list == null || list.size() == 0){
                return;
            }


            List<OverLimit> limits = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.0");

            for (OnenetDev4 dev:list) {
                Long deviceFour = globalOnenetTime.getDeviceFour();
                if (deviceFour == null){
                    deviceFour = 0L;
                }
                OverLimit overLimit = new OverLimit();

                if (dev.getTime()>deviceFour+1000*60*60 && dev.getLv() != null && dev.getLv() > 3){
                    overLimit.setDevice("device4");
                    overLimit.setUpdateTime(dev.getUpdateTime());
                    overLimit.setLv((double)dev.getLv());
                    overLimit.setLimitType("超设计标准");
                    overLimit.setLimitState("+"+df.format((double)dev.getLv()-3)+"m");
                    globalOnenetTime.setDeviceFour(dev.getTime());
                    limits.add(overLimit);
                }


            }

            if (limits.size() > 0){
                saveBatch(limits);
            }

        }catch (Exception e){
            logger.error("获取超限设备4数据发生异常",e);
        }
    }

    @Override
    @Async("asyncExecutor")
    public void saveDeviceFive(List<OnenetDev5> list) {
        try {
            if (list == null || list.size() == 0){
                return;
            }



            List<OverLimit> limits = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.0");

            for (OnenetDev5 dev:list) {
                Long deviceFive = globalOnenetTime.getDeviceFive();
                if (deviceFive == null){
                    deviceFive = 0L;
                }
                OverLimit overLimit = new OverLimit();

                if (dev.getTime()>deviceFive+1000*60*60 && dev.getLv() != null && dev.getLv() > 677){
                    overLimit.setDevice("device5");
                    overLimit.setUpdateTime(dev.getUpdateTime());
                    overLimit.setLv((double)dev.getLv());
                    overLimit.setLimitType("超讯限水位");
                    overLimit.setLimitState("+"+df.format((double)dev.getLv()-677)+"m");
                    globalOnenetTime.setDeviceFive(dev.getTime());
                    limits.add(overLimit);
                }

            }

            if (limits.size() > 0){
                saveBatch(limits);
            }

        }catch (Exception e){
            logger.error("获取超限设备5数据发生异常",e);
        }
    }

    @Override
    @Async("asyncExecutor")
    public void saveDeviceSix(List<OnenetDev6> list) {
        try {
            if (list == null || list.size() == 0){
                return;
            }



            List<OverLimit> limits = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("0.0");

            for (OnenetDev6 dev:list) {
                Long deviceSix = globalOnenetTime.getDeviceSix();
                if (deviceSix == null){
                    deviceSix = 0L;
                }
                OverLimit overLimit = new OverLimit();

                if (dev.getTime()>deviceSix+1000*60*60 && dev.getLv() != null && dev.getLv() > 3){
                    overLimit.setDevice("device6");
                    overLimit.setUpdateTime(dev.getUpdateTime());
                    overLimit.setLv((double)dev.getLv());
                    overLimit.setLimitType("超设计标准");
                    overLimit.setLimitState("+"+df.format((double)dev.getLv()-3)+"m");
                    globalOnenetTime.setDeviceSix(dev.getTime());
                    limits.add(overLimit);
                }

            }

            if (limits.size() > 0){
                saveBatch(limits);
            }

        }catch (Exception e){
            logger.error("获取超限设备6数据发生异常",e);
        }
    }


    @Override
    public OverResponse selectLatestOver() {
        OverResponse response = new OverResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {

            QueryWrapper<OverLimit> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 20");
            List<OverLimit> limits = overLimitMapper.selectList(queryWrapper);
            if (limits == null || limits.size() == 0){
                meta.setStatus(400);
                meta.setMsg("获取onenet超限数据失败");
                return response;
            }

            List<OverLimitUnit> units = new ArrayList<>();
            for (OverLimit ol:limits) {
                OverLimitUnit unit = this.limitToUnit(ol);
                units.add(unit);
            }
            meta.setStatus(200);
            meta.setMsg("获取onenet超限数据成功");
            response.setUnits(units);
            return response;


        }catch (Exception e){
            logger.error("获取onenet超限数据发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取onenet超限数据失败");
        return response;
    }


    @Override
    public OverResponse pagedQueryOver(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<OverLimit> page = new Page<>(pageNum, pageSize);
            QueryWrapper<OverLimit> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("update_time",start,end);
            }
            queryWrapper.orderByAsc("update_time");
            Page<OverLimit> limitPage = overLimitMapper.selectPage(page, queryWrapper);
            return this.pageToRes(limitPage,pageNum,pageSize);



        }catch (Exception e){
            logger.error("获取onenet超限分页数据发生异常",e);
        }
        return null;
    }



    public OverLimitUnit limitToUnit(OverLimit ol){
        try {

            if (ol == null){
                return null;
            }


            HashMap<String, String> map = new HashMap<>();
            map.put("device4","两河口");
            map.put("device5","大坝");
            map.put("device6","下游");



            OverLimitUnit unit = new OverLimitUnit();
            unit.setUpdateTime(DateLocalUtils.parseDateToStr(ol.getUpdateTime()));
            unit.setLocation(map.get(ol.getDevice()));
            unit.setDevice(ol.getDevice());
            unit.setLv(ol.getLv());
            unit.setLimitType(ol.getLimitType());
            unit.setLimitState(ol.getLimitState());


            return unit;
        }catch (Exception e){
            logger.error("onenet超限转换发生异常",e);
        }
        return null;
    }


    public OverResponse pageToRes(IPage<OverLimit> page, Integer pageNum, Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }


            OverResponse response = new OverResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取onenet超限分页数据成功");
            response.setMeta(meta);
            List<OverLimitUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;

            for (OverLimit ol:page.getRecords()) {

                OverLimitUnit unit = limitToUnit(ol);

                unit.setOrderNum(startIndex++);

                units.add(unit);
            }

            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            response.setUnits(units);
            return response;

        }catch (Exception e){
            logger.error("onenet超限转换发生异常",e);
        }
        return null;
    }


}
