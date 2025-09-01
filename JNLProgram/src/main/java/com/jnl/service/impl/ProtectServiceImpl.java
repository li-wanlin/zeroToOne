package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.ProtectManage;
import com.jnl.mapper.ProtectMapper;
import com.jnl.service.ProtectService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.protectVo.ProtectResponse;
import com.jnl.vo.protectVo.ProtectUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProtectServiceImpl extends ServiceImpl<ProtectMapper, ProtectManage> implements ProtectService {

    @Resource
    ProtectMapper protectMapper;


    private static final Logger logger = LoggerFactory.getLogger(ProtectServiceImpl.class);



    @Override
    public ProtectResponse displayInfo() {
        try {
            QueryWrapper<ProtectManage> queryWrapper = new QueryWrapper<>();
            LocalDate start = LocalDate.now().withDayOfYear(1);
            LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
            queryWrapper.between("plan_time",start,end);

            List<ProtectManage> protectManages = protectMapper.selectList(queryWrapper);
            return this.ProsToRes(protectManages);

        }catch (Exception e){
            logger.error("获取保护管理当年数据发生异常",e);
        }

        return null;
    }

    @Override
    public Boolean insertByInfo(ProtectUnit unit) {
        try {
            if (unit == null || unit.getPlanTime() == null){
                return false;
            }

            ProtectManage protectManage = this.unitToPro(unit);
            return save(protectManage);

        }catch (Exception e){
            logger.error("插入保护管理数据发生异常",e);

        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(ProtectUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            return removeById(unit.getId());


        }catch (Exception e){
            logger.error("删除保护管理数据发生异常");
        }
        return false;
    }



    @Override
    public Boolean updateByInfo(ProtectUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            return updateById(this.unitToPro(unit));

        }catch (Exception e){
            logger.error("更新保护管理数据发生异常",e);
        }
        return false;
    }



    @Override
    public ProtectResponse pagedQuery(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            QueryWrapper<ProtectManage> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByAsc("plan_time");
            Page<ProtectManage> page = new Page<>(pageNum, pageSize);
            Page<ProtectManage> managePage = protectMapper.selectPage(page, queryWrapper);

            return this.pageToRes(managePage,pageNum,pageSize);


        }catch (Exception e){
            logger.error("获取保护管理分页数据发生异常",e);
        }

        return null;
    }

    @Override
    public ProtectResponse pagedQueryByYear(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageNum == 0 || pageSize == null || pageSize == 0){
                return null;
            }

            Date end = DateLocalUtils.parseYearToEnd(year);
            Date start = DateLocalUtils.parseYearToStart(year);


            QueryWrapper<ProtectManage> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("plan_time",start,end);
            queryWrapper.orderByAsc("plan_time");
            Page<ProtectManage> page = new Page<>(pageNum, pageSize);
            Page<ProtectManage> managePage = protectMapper.selectPage(page, queryWrapper);

            return this.pageToRes(managePage,pageNum,pageSize);


        }catch (Exception e){
            logger.error("获取保护管理按年分页数据发生异常",e);
        }

        return null;
    }


    public ProtectResponse ProsToRes(List<ProtectManage> protectManages){
        try {
            if (protectManages == null || protectManages.size() == 0){
                return null;
            }

            ProtectResponse response = new ProtectResponse();
            Meta meta = new Meta();
            meta.setMsg("获取保护管理当年数据成功");
            meta.setStatus(200);
            response.setMeta(meta);

            List<ProtectUnit> units = new ArrayList<>();

            for (ProtectManage pro:protectManages) {
                ProtectUnit unit = new ProtectUnit();
                unit.setId(pro.getId());
                unit.setProtectType(pro.getProtectType());
                unit.setProtectName(pro.getProtectName());
                unit.setPlanTime(DateLocalUtils.parseDateToStrTwo(pro.getPlanTime()));
                unit.setActTime(DateLocalUtils.parseDateToStrTwo(pro.getActTime()));
                unit.setPlanAmount(pro.getPlanAmount());
                unit.setActAmount(pro.getActAmount());
                unit.setRemainPro(pro.getRemainPro());
                unit.setDevelopment(pro.getDevelopment());
                unit.setReformState(pro.getReformState());
                unit.setReformTime(DateLocalUtils.parseDateToStrTwo(pro.getReformTime()));
                unit.setLeaderInfo(pro.getLeaderInfo());
                unit.setProNotes(pro.getProNotes());

                units.add(unit);
            }

            response.setProtectUnits(units);
            response.setYearCount(protectManages.size());
            response.setPlanCount(protectManages.stream().filter(protectManage -> protectManage.getPlanAmount() != null).mapToDouble(ProtectManage::getPlanAmount).sum());
            return response;



        }catch (Exception e){
            logger.error("转换保护管理当年数据发生异常",e);
        }

        return null;
    }


    public ProtectManage unitToPro(ProtectUnit unit){
        try {
            if (unit == null){
                return null;
            }
            ProtectManage pro = new ProtectManage();
            pro.setId(unit.getId());

            if (unit.getProtectType() != null){
                pro.setProtectType(unit.getProtectType());
            }

            if (unit.getProtectName() != null){
                pro.setProtectName(unit.getProtectName());
            }

            if (unit.getPlanTime() != null){
                pro.setPlanTime(DateLocalUtils.parseGiveStrToDate(unit.getPlanTime()));
            }

            if (unit.getActTime() != null){
                pro.setActTime(DateLocalUtils.parseGiveStrToDate(unit.getActTime()));
            }

            if (unit.getPlanAmount() != null){
                pro.setPlanAmount(unit.getPlanAmount());
            }

            if (unit.getActAmount() != null){
                pro.setActAmount(unit.getActAmount());
            }

            if (unit.getRemainPro() != null){
                pro.setRemainPro(unit.getRemainPro());
            }

            if (unit.getDevelopment() != null){
                pro.setDevelopment(unit.getDevelopment());
            }

            if (unit.getReformState() != null){
                pro.setReformState(unit.getReformState());
            }

            if (unit.getReformTime() != null){
                pro.setReformTime(DateLocalUtils.parseGiveStrToDate(unit.getReformTime()));
            }

            if (unit.getLeaderInfo() != null){
                pro.setLeaderInfo(unit.getLeaderInfo());
            }

            if (unit.getProNotes() != null){
                pro.setProNotes(unit.getProNotes());
            }

            pro.setUpdateTime(new Date());

            return pro;

        }catch (Exception e){
            logger.error("转换数据发生异常",e);
        }
        return null;
    }



    public ProtectResponse pageToRes(IPage<ProtectManage> page,Integer pageNum,Integer pageSize){
        try {
            if (page == null || page.getRecords().size() == 0){
                return null;
            }

            ProtectResponse response = new ProtectResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取保护管理分页数据成功");
            response.setMeta(meta);

            ArrayList<ProtectUnit> units = new ArrayList<>();
            Integer startIndex = (pageNum - 1) * pageSize + 1;

            for (ProtectManage pro:page.getRecords()) {
                ProtectUnit unit = new ProtectUnit();
                unit.setId(pro.getId());
                unit.setOrderNum(startIndex++);
                unit.setProtectType(pro.getProtectType());
                unit.setProtectName(pro.getProtectName());
                unit.setPlanTime(DateLocalUtils.parseDateToStrTwo(pro.getPlanTime()));
                unit.setActTime(DateLocalUtils.parseDateToStrTwo(pro.getActTime()));
                unit.setPlanAmount(pro.getPlanAmount());
                unit.setActAmount(pro.getActAmount());
                unit.setRemainPro(pro.getRemainPro());
                unit.setDevelopment(pro.getDevelopment());
                unit.setReformState(pro.getReformState());
                unit.setReformTime(DateLocalUtils.parseDateToStrTwo(pro.getReformTime()));
                unit.setLeaderInfo(pro.getLeaderInfo());
                unit.setProNotes(pro.getProNotes());

                units.add(unit);
            }

            response.setProtectUnits(units);
            response.setCurrentPage(pageNum);
            response.setTotalPage((int)page.getPages());
            response.setTotalCount((int)page.getTotal());
            return response;


        }catch (Exception e){
            logger.error("在转换分页数据时发生异常",e);
        }
        return null;
    }


}
