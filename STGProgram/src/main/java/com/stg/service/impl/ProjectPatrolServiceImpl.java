package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.ProjectPatrol;
import com.stg.mapper.ProjectPatrolMapper;
import com.stg.service.ProjectPatrolService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.patrolVo.ProPatrolResponse;
import com.stg.vo.patrolVo.ProPatrolUnit;
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
public class ProjectPatrolServiceImpl extends ServiceImpl<ProjectPatrolMapper, ProjectPatrol> implements ProjectPatrolService {


    @Resource
    ProjectPatrolMapper projectPatrolMapper;


    @Resource
    ProjectImgServiceImpl projectImgService;


    private static final Logger logger = LoggerFactory.getLogger(ProjectPatrolServiceImpl.class);


    @Override
    public List<ProPatrolUnit> selectLatestFifty() {
        try {
            QueryWrapper<ProjectPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("finish_time");
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 50");

            List<ProjectPatrol> selectList = projectPatrolMapper.selectList(queryWrapper);

            return this.tranProToPaInfo(selectList);
        }catch (Exception e){
            logger.error("获取工程巡查最新50条数据发生异常",e);
        }
        return null;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByInfo(ProPatrolUnit proPatrolUnit, MultipartFile[] files) {
        try {

            if (proPatrolUnit == null
                    || proPatrolUnit.getPlanTime() == null){
                return false;
            }

            ProjectPatrol projectPatrol = this.tranUnitToPro(proPatrolUnit);
            boolean save = save(projectPatrol);

            if (save && files != null){
                projectImgService.insertByInfo(projectPatrol.getId(),files);
            }

            return save;

        }catch (Exception e){
            logger.error("插入工程巡查数据发生异常",e);
            throw new RuntimeException("数据插入失败", e);
        }
    }



    @Override
    public Boolean deleteByInfo(ProPatrolUnit proPatrolUnit) {
        try {
            if (proPatrolUnit == null || proPatrolUnit.getId() == null){
                return false;
            }

            boolean remove = removeById(proPatrolUnit.getId());

            if (remove){
                projectImgService.deleteByImgId(proPatrolUnit.getId());
            }

            return remove;
        }catch (Exception e){
            logger.error("删除工程巡查数据发生异常",e);
        }

        return false;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByInfo(ProPatrolUnit proPatrolUnit,MultipartFile[] files) {
        try {
            if (proPatrolUnit == null || proPatrolUnit.getId() == null){
                return false;
            }
            ProjectPatrol projectPatrol = this.tranUnitToPro(proPatrolUnit);

            boolean update = updateById(projectPatrol);
            if (update && files != null){
                projectImgService.deleteByImgId(projectPatrol.getId());
                projectImgService.insertByInfo(projectPatrol.getId(),files);
            }

            return update;
        }catch (Exception e){
            logger.error("修改工程巡查数据发生异常",e);
            throw new RuntimeException("数据更新失败", e);
        }
    }



    @Override
    public ProPatrolResponse pagedQuery(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null || pageSize == null || pageNum == 0 || pageSize == 0){
                return null;
            }

            //创建分页对象
            Page<ProjectPatrol> page = new Page<>(pageNum, pageSize);
            //创建查询条件
            QueryWrapper<ProjectPatrol> queryWrapper = new QueryWrapper<>();
            //执行分页查询
            Page<ProjectPatrol> patrolPage = projectPatrolMapper.selectPage(page, queryWrapper);


            return this.pageToResponse(patrolPage,pageNum,pageSize);
        }catch (Exception e){
            logger.error("获取工程巡查分页查询发生异常",e);
        }

        return null;
    }



    @Override
    public ProPatrolResponse pagedQueryByYear(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageSize == null || pageNum == 0 || pageSize == 0 || year == null){
                return null;
            }

            Date start = DateLocalUtils.parseYearToStart(year);
            Date end = DateLocalUtils.parseYearToEnd(year);

            //创建分页对象
            Page<ProjectPatrol> page = new Page<>(pageNum, pageSize);
            //创建查询条件
            QueryWrapper<ProjectPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("plan_time",start,end);

            Page<ProjectPatrol> patrolPage = projectPatrolMapper.selectPage(page, queryWrapper);

            return this.pageToResponse(patrolPage,pageNum,pageSize);

        }catch (Exception e){
            logger.error("获取按年工程巡查分页查询发生异常",e);
        }
        return null;
    }



    @Override
    public List<String> getFileNames(Integer imgId) {
        try {
            if (imgId == null){
                return null;
            }

            return projectImgService.getFileNames(imgId);


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



    @Override
    public byte[] previewByName(String fileName) {
        return projectImgService.previewByName(fileName);
    }


    public List<ProPatrolUnit> tranProToPaInfo(List<ProjectPatrol> selectList){
        try {
            if (selectList == null || selectList.size() == 0){
                return null;
            }

            List<ProPatrolUnit> units = new ArrayList<>();

            for (ProjectPatrol pro:selectList) {
                ProPatrolUnit unit = new ProPatrolUnit();
                unit.setId(pro.getId());
                unit.setPatrolArea(pro.getPatrolArea());
                unit.setPlanTime(DateLocalUtils.parseGiveDaToStr(pro.getPlanTime(),"yyyy-MM-dd"));
                unit.setFinishTime(DateLocalUtils.parseGiveDaToStr(pro.getFinishTime(),"yyyy-MM-dd"));
                unit.setPatrolState(pro.getPatrolState());
                unit.setReformState(pro.getReformState());
                unit.setReformTime(DateLocalUtils.parseGiveDaToStr(pro.getReformTime(),"yyyy-MM-dd"));
                unit.setIfReform(pro.getIfReform());
                unit.setPatrolPerson(pro.getPatrolPerson());
                unit.setLeaderInfo(pro.getLeaderInfo());
                unit.setPatrolNotes(pro.getPatrolNotes());


                units.add(unit);
            }
            return units;

        }catch (Exception e){
            logger.error("工程巡查对象转换发生异常",e);
        }
        return null;
    }


    public ProjectPatrol tranUnitToPro(ProPatrolUnit unit){
        try {
            ProjectPatrol pro = new ProjectPatrol();
            pro.setId(unit.getId());

            if (unit.getPatrolArea() != null){
                pro.setPatrolArea(unit.getPatrolArea());
            }

            if (unit.getPlanTime() != null){
                pro.setPlanTime(DateLocalUtils.parseGiveStrToDate(unit.getPlanTime()));
            }

            if (unit.getFinishTime() != null){
                pro.setFinishTime(DateLocalUtils.parseGiveStrToDate(unit.getFinishTime()));
            }

            if (unit.getPatrolState() != null){
                pro.setPatrolState(unit.getPatrolState());
            }

            if (unit.getReformState() != null){
                pro.setReformState(unit.getReformState());
            }

            if (unit.getReformTime() != null){
                pro.setReformTime(DateLocalUtils.parseGiveStrToDate(unit.getReformTime()));
            }

            if (unit.getIfReform() != null){
                pro.setIfReform(unit.getIfReform());
            }

            if (unit.getPatrolPerson() != null){
                pro.setPatrolPerson(unit.getPatrolPerson());
            }

            if (unit.getLeaderInfo() != null){
                pro.setLeaderInfo(unit.getLeaderInfo());
            }

            if (unit.getPatrolNotes() != null){
                pro.setPatrolNotes(unit.getPatrolNotes());
            }


            pro.setUpdateTime(new Date());

            return pro;
        }catch (Exception e){
            logger.error("转换为工程巡查对象失败",e);
        }

        return null;
    }



    public ProPatrolResponse pageToResponse(IPage<ProjectPatrol> patrolPage,Integer pageNum,Integer pageSize){
        try {
            if (patrolPage == null || patrolPage.getRecords().size() == 0){
                return null;
            }


            ProPatrolResponse response = new ProPatrolResponse();
            Meta meta = new Meta();
            meta.setStatus(200);
            meta.setMsg("获取工程巡查分页数据成功");
            response.setMeta(meta);


            List<ProjectPatrol> patrolList = patrolPage.getRecords();

            int startIndex = (pageNum - 1) * pageSize + 1;


            List<ProPatrolUnit> units = new ArrayList<>();
            for (ProjectPatrol pro:patrolList) {
                ProPatrolUnit unit = new ProPatrolUnit();
                unit.setId(pro.getId());
                unit.setOrderNum(startIndex++);
                unit.setPatrolArea(pro.getPatrolArea());
                unit.setPlanTime(DateLocalUtils.parseGiveDaToStr(pro.getPlanTime(),"yyyy-MM-dd"));
                unit.setFinishTime(DateLocalUtils.parseGiveDaToStr(pro.getFinishTime(),"yyyy-MM-dd"));
                unit.setPatrolState(pro.getPatrolState());
                unit.setReformState(pro.getReformState());
                unit.setReformTime(DateLocalUtils.parseGiveDaToStr(pro.getReformTime(),"yyyy-MM-dd"));
                unit.setIfReform(pro.getIfReform());
                unit.setPatrolPerson(pro.getPatrolPerson());
                unit.setLeaderInfo(pro.getLeaderInfo());
                unit.setPatrolNotes(pro.getPatrolNotes());
                units.add(unit);
            }

            response.setCurrentPage(pageNum);
            response.setTotalPage((int)patrolPage.getPages());
            response.setTotalCount((int)patrolPage.getTotal());
            response.setProPatrolUnits(units);
            response.setPlanCount((int) patrolList.stream().filter(projectPatrol -> projectPatrol.getPlanTime() != null).count());
            response.setFinishCount((int) patrolList.stream().filter(projectPatrol -> projectPatrol.getFinishTime() != null).count());
            response.setCompleteCount((int) patrolList.stream().filter(projectPatrol -> "是".equals(projectPatrol.getIfReform())).count());
            response.setPendingCount((int) patrolList.stream().filter(projectPatrol -> "否".equals(projectPatrol.getIfReform())).count());
            return response;

        }catch (Exception e){
            logger.error("在工程巡查分页数据进行转换时发生异常",e);
        }
        return null;
    }


}
