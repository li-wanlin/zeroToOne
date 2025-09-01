package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.AnnualRepair;
import com.jnl.mapper.AnnualMapper;
import com.jnl.service.AnnualService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.annualVo.AnnualResponse;
import com.jnl.vo.annualVo.AnnualUnit;
import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnnualServiceImpl extends ServiceImpl<AnnualMapper, AnnualRepair> implements AnnualService {

    @Resource
    AnnualMapper annualMapper;


    private static final Logger logger = LoggerFactory.getLogger(AnnualServiceImpl.class);



    @Override
    public Boolean insertByInfo(AnnualUnit unit) {
        try {
            if (unit == null || unit.getPlanTime() == null){
                return false;
            }

            return save(this.unitToAn(unit));

        }catch (Exception e){
            logger.error("增加水库岁修数据发生异常",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(AnnualUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            return removeById(unit.getId());


        }catch (Exception e){
            logger.error("删除水库岁修数据发生异常",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(AnnualUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            return updateById(this.unitToAn(unit));


        }catch (Exception e){
            logger.error("更新水库岁修数据发生异常",e);
        }

        return false;
    }

    @Override
    public AnnualResponse pagedQuery(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Page<AnnualRepair> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AnnualRepair> queryWrapper = new QueryWrapper<>();
            if (year != null){
                Date start = DateLocalUtils.parseYearToStart(year);
                Date end = DateLocalUtils.parseYearToEnd(year);
                queryWrapper.between("plan_time",start,end);
            }

            queryWrapper.orderByAsc("plan_time");

            Page<AnnualRepair> repairPage = annualMapper.selectPage(page, queryWrapper);

            return this.pageToRes(repairPage,pageNum,pageSize);


        }catch (Exception e){
            logger.error("获取水库岁修分页数据发生异常",e);
        }
        return null;
    }



    public AnnualRepair unitToAn(AnnualUnit unit){
        try {
            if (unit == null){
                return null;
            }
            AnnualRepair an = new AnnualRepair();
            an.setId(unit.getId());

            if (unit.getPlanTime() != null){
                an.setPlanTime(DateLocalUtils.parseGiveStrToDate(unit.getPlanTime()));
            }

            if (unit.getPlanContent() != null){
                an.setPlanContent(unit.getPlanContent());
            }

            if (unit.getPlanAmount() != null){
                an.setPlanAmount(unit.getPlanAmount());
            }

            if (unit.getAppTime() != null){
                an.setAppTime(DateLocalUtils.parseGiveStrToDate(unit.getAppTime()));
            }

            if (unit.getAppUnit() != null){
                an.setAppUnit(unit.getAppUnit());
            }

            if (unit.getAppAmount() != null){
                an.setAppAmount(unit.getAppAmount());
            }

            if (unit.getDevelopment() != null){
                an.setDevelopment(unit.getDevelopment());
            }

            if (unit.getDepAmount() != null){
                an.setDepAmount(unit.getDepAmount());
            }

            if (unit.getRemainPro() != null){
                an.setRemainPro(unit.getRemainPro());
            }

            if (unit.getProNotes() != null){
                an.setProNotes(unit.getProNotes());
            }

            an.setUpdateTime(new Date());

            return an;


        }catch (Exception e){
            logger.error("水库岁修转换发生异常",e);
        }
        return null;
    }



    public AnnualResponse pageToRes(IPage<AnnualRepair> page,Integer pageNum,Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }


            AnnualResponse response = new AnnualResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取水库岁修分页数据成功");
            response.setMeta(meta);
            List<AnnualUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;

            for (AnnualRepair an:page.getRecords()) {
                AnnualUnit unit = new AnnualUnit();
                unit.setId(an.getId());
                unit.setOrderNum(startIndex++);
                unit.setPlanTime(DateLocalUtils.parseDateToStrTwo(an.getPlanTime()));
                unit.setPlanContent(an.getPlanContent());
                unit.setPlanAmount(an.getPlanAmount());
                unit.setAppTime(DateLocalUtils.parseDateToStrTwo(an.getAppTime()));
                unit.setAppUnit(an.getAppUnit());
                unit.setAppAmount(an.getAppAmount());
                unit.setDevelopment(an.getDevelopment());
                unit.setDepAmount(an.getDepAmount());
                unit.setRemainPro(an.getRemainPro());
                unit.setProNotes(an.getProNotes());

                units.add(unit);
            }

            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            response.setAnnualUnits(units);
            return response;

        }catch (Exception e){
            logger.error("水库岁修转换发生异常",e);
        }
        return null;
    }



}
