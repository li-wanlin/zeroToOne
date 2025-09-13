package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.*;
import com.stg.mapper.OverLimitMapper;
import com.stg.service.OverLimitService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.fourPreventVo.OverLimitUnit;
import com.stg.vo.fourPreventVo.OverResponse;
import com.stg.vo.functionVo.GlobalOnenetTime;
import com.stg.vo.functionVo.Meta;
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
                if (unit != null){
                    units.add(unit);
                }

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

            if (ol.getDevice().equals("device24")){
                return null;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put("device22","大坝");
            map.put("device23","水库下游");




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
