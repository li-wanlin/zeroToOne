package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.EduTrain;
import com.jnl.mapper.EduTrainImgMapper;
import com.jnl.mapper.EduTrainMapper;
import com.jnl.service.EduTrainService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class EduTrainServiceImpl extends ServiceImpl<EduTrainMapper, EduTrain> implements EduTrainService {


    @Resource
    EduTrainMapper eduTrainMapper;


    @Resource
    EduTrainImgMapper eduTrainImgMapper;


    @Resource
    EduTrainImgServiceImpl eduTrainImgService;


    private static final Logger logger = LoggerFactory.getLogger(EduTrainServiceImpl.class);



    @Override
    public List<EduTrain> selectLatestFifty() {
        try {

            QueryWrapper<EduTrain> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 50");

            return eduTrainMapper.selectList(queryWrapper);
        }catch (Exception e){
            logger.error("获取教育培训最新50条数据发生异常",e);
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean InsertByEdu(EduTrain edu, MultipartFile[] files) {
        try {
            if (edu == null || edu.getEduTime() == null || StringUtils.isAnyEmpty(edu.getEduName(),edu.getEduUnit(),edu.getEduContent())){
                return false;
            }

            edu.setUpdateTime(new Date());

            boolean save = save(edu);

            if (save && files != null){
                Integer imgId = edu.getId();
                eduTrainImgService.insertByInfo(imgId, files);
            }


            return save;
        }catch (Exception e){
            logger.error("插入教育培训数据发生异常",e);
            throw new RuntimeException("数据插入失败", e);
        }
    }

    @Override
    public Boolean DeleteByEdu(EduTrain edu) {

        try {

            if (edu == null || edu.getId() == null){
                return false;
            }

            boolean remove = removeById(edu.getId());

            if (remove){
                eduTrainImgService.deleteByImgId(edu.getId());
            }

            return remove;

        }catch (Exception e){
            logger.error("删除教育培训数据发生异常",e);
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean UpdateByEdu(EduTrain edu,MultipartFile[] files) {
        try {
            if (edu == null || edu.getId() == null){
                return false;
            }

            edu.setUpdateTime(new Date());
            boolean update = updateById(edu);
            if (update && files != null){
                eduTrainImgService.deleteByImgId(edu.getId());
                eduTrainImgService.insertByInfo(edu.getId(),files);
            }


            return update;
        }catch (Exception e){
            logger.error("修改教育培训数据失败",e);
            throw new RuntimeException("数据更新失败", e);
        }
    }

    @Override
    public IPage<EduTrain> pagedQuery(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null || pageSize == null || pageNum == 0 || pageSize == 0){
                return null;
            }

            QueryWrapper<EduTrain> queryWrapper = new QueryWrapper<>();
            Page<EduTrain> page = new Page<>(pageNum, pageSize);
            return eduTrainMapper.selectPage(page, queryWrapper);

        }catch (Exception e){
            logger.error("分页查询教育培训发生异常",e);
        }
        return null;
    }


    @Override
    public IPage<EduTrain> pagedQueryByYear(Integer pageNum, Integer pageSize, String year) {
        try {
            if (pageNum == null || pageSize == null || pageNum == 0 || pageSize == 0 || StringUtils.isEmpty(year)){
                return null;
            }
            QueryWrapper<EduTrain> queryWrapper = new QueryWrapper<>();
            queryWrapper.likeRight("edu_time",year);
            Page<EduTrain> page = new Page<>(pageNum, pageSize);
            return eduTrainMapper.selectPage(page,queryWrapper);

        }catch (Exception e){
            logger.error("通过时间分页查询教育培训发生异常",e);
        }
        return null;
    }



    @Override
    public List<String> getFileNames(Integer imgId) {
        try {
            if (imgId == null){
                return null;
            }

            return eduTrainImgService.getFileNames(imgId);


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



    @Override
    public byte[] previewByName(String fileName) {
        return eduTrainImgService.previewByName(fileName);
    }
}
