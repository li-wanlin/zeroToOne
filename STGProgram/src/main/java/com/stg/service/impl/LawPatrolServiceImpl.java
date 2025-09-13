package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.LawPatrol;
import com.stg.mapper.LawPatrolMapper;
import com.stg.service.LawPatrolImgService;
import com.stg.service.LawPatrolService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.patrolVo.PatrolInfo;
import org.apache.commons.lang3.StringUtils;
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
public class LawPatrolServiceImpl extends ServiceImpl<LawPatrolMapper, LawPatrol>  implements LawPatrolService {

    @Resource
    LawPatrolMapper lawPatrolMapper;


    @Resource
    LawPatrolImgService lawPatrolImgService;


    private static final Logger logger = LoggerFactory.getLogger(LawPatrolServiceImpl.class);


    @Override
    public List<PatrolInfo> selectLatestFifty() {
        try {
            QueryWrapper<LawPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 50");

            List<LawPatrol> selectList = lawPatrolMapper.selectList(queryWrapper);

            return this.tranReToPaInfo(selectList);
        }catch (Exception e){
            logger.error("获取执法巡查最新50条数据发生异常",e);
        }
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean InsertByInfo(PatrolInfo patrolInfo, MultipartFile[] files) {
        try {

            if (patrolInfo == null || patrolInfo.getPatrolTime() == null
                    || patrolInfo.getPatrolType() == null || patrolInfo.getIfComplete() == null
                    || patrolInfo.getPatrolState() == null){
                return false;
            }

            LawPatrol lawPatrol = this.tranPaInfoToLaw(patrolInfo);
            if (lawPatrol != null){
                boolean save = save(lawPatrol);
                Integer lawId = lawPatrol.getId();
                lawPatrolImgService.insertByInfo(lawId, files);
                return save;
            }
        }catch (Exception e){
            logger.error("插入执法巡查数据发生异常",e);
            throw new RuntimeException("数据插入失败", e);
        }
        return false;
    }




    @Override
    public Boolean DeleteByInfo(PatrolInfo patrolInfo) {
        try {
            if (patrolInfo == null || patrolInfo.getId() == null){
                return false;
            }

            boolean remove = removeById(patrolInfo.getId());

            if (remove){
                lawPatrolImgService.deleteByLawId(patrolInfo.getId());
            }

            return remove;
        }catch (Exception e){
            logger.error("删除库区巡查数据发生异常",e);
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean UpdateByInfo(PatrolInfo patrolInfo,MultipartFile[] files) {
        try {
            if (patrolInfo == null ||patrolInfo.getId() == null){
                return false;
            }

            LawPatrol lawPatrol = this.tranPaInfoToLaw(patrolInfo);

            boolean update = updateById(lawPatrol);

            if (update && files != null){

                lawPatrolImgService.deleteByLawId(patrolInfo.getId());
                lawPatrolImgService.insertByInfo(patrolInfo.getId(),files);

            }
            return update;
        }catch (Exception e){
            logger.error("更新库区巡查数据发生异常",e);
            throw new RuntimeException("数据更新失败", e);
        }

    }

    @Override
    public IPage<LawPatrol> pagedQuery(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null || pageSize == null || pageNum == 0 || pageSize == 0){
                return null;
            }

            //创建分页对象
            Page<LawPatrol> page = new Page<>(pageNum, pageSize);
            //创建查询条件
            QueryWrapper<LawPatrol> queryWrapper = new QueryWrapper<>();
            //执行分页查询
            Page<LawPatrol> patrolPage = lawPatrolMapper.selectPage(page, queryWrapper);

            return patrolPage;
        }catch (Exception e){
            logger.error("获取库区巡查分页查询发生异常",e);
        }

        return null;
    }

    @Override
    public IPage<LawPatrol> pagedQueryByYear(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageSize == null || pageNum == 0
                    || pageSize == 0 || StringUtils.isEmpty(year)){
                return null;
            }

            //创建分页对象
            Page<LawPatrol> page = new Page<>(pageNum, pageSize);
            //创建查询条件
            QueryWrapper<LawPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.likeRight("patrol_time",year);

            //执行分页查询
            Page<LawPatrol> patrolPage = lawPatrolMapper.selectPage(page, queryWrapper);

            return patrolPage;
        }catch (Exception e){
            logger.error("获取执法巡查分页查询发生异常",e);
        }

        return null;
    }

    @Override
    public List<String> getFileNames(Integer lawId) {
        try {
            if (lawId == null){
                return null;
            }

            return lawPatrolImgService.getFileNames(lawId);


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }

    @Override
    public byte[] previewByName(String fileName) {
        return lawPatrolImgService.previewByName(fileName);
    }


    public List<PatrolInfo> tranReToPaInfo(List<LawPatrol> selectList){
        try{
            if (selectList == null || selectList.size() == 0){
                return null;
            }

            List<PatrolInfo> patrolInfos = new ArrayList<>();

            for (LawPatrol re:selectList) {
                PatrolInfo patrolInfo = new PatrolInfo();
                patrolInfo.setId(re.getId());
                patrolInfo.setPatrolTime(DateLocalUtils.parseGiveDaToStr(re.getPatrolTime(),"yyyy-MM-dd"));
                patrolInfo.setPatrolType(re.getPatrolType());
                patrolInfo.setPatrolArea(re.getPatrolArea());
                patrolInfo.setPatrolState(re.getPatrolState());
                patrolInfo.setPatrolProblem(re.getPatrolProblem());
                patrolInfo.setSolutions(re.getSolutions());
                patrolInfo.setPatrolLeader(re.getPatrolLeader());
                patrolInfo.setPatrolPerson(re.getPatrolPerson());
                patrolInfo.setIfComplete(re.getIfComplete());
                patrolInfo.setCompleteState(re.getCompleteState());
                patrolInfo.setPatrolNotes(re.getPatrolNotes());
                patrolInfos.add(patrolInfo);
            }
            return patrolInfos;
        }catch (Exception e){
            logger.error("库区巡查对象转换发生异常",e);
        }
        return null;
    }


    public LawPatrol tranPaInfoToLaw(PatrolInfo patrolInfo){
        try{

            if (patrolInfo == null || patrolInfo.getPatrolTime() == null){
                return null;
            }

            LawPatrol law = new LawPatrol();
            law.setId(patrolInfo.getId());
            law.setPatrolTime(DateLocalUtils.parseGiveStrToDate(patrolInfo.getPatrolTime()));
            law.setPatrolType(patrolInfo.getPatrolType());
            law.setPatrolArea(patrolInfo.getPatrolArea());
            law.setPatrolState(patrolInfo.getPatrolState());
            law.setPatrolProblem(patrolInfo.getPatrolProblem());
            law.setSolutions(patrolInfo.getSolutions());
            law.setPatrolLeader(patrolInfo.getPatrolLeader());
            law.setPatrolPerson(patrolInfo.getPatrolPerson());
            law.setIfComplete(patrolInfo.getIfComplete());
            law.setCompleteState(patrolInfo.getCompleteState());
            law.setPatrolNotes(patrolInfo.getPatrolNotes());
            law.setUpdateTime(new Date());
            return law;
        }catch (Exception e){
            logger.error("执法巡查转换对象发生异常",e);
        }


        return null;
    }
}
