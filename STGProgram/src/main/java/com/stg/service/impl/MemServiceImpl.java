package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.ProMemoirs;
import com.stg.mapper.MemMapper;
import com.stg.service.MemService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.memoirsVo.MemUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MemServiceImpl extends ServiceImpl<MemMapper, ProMemoirs> implements MemService {

    @Resource
    MemMapper memMapper;

    private static final Logger logger = LoggerFactory.getLogger(MemServiceImpl.class);



    @Override
    public Boolean insertByInfo(MemUnit unit) {
        try {
            if (unit == null || unit.getInputTime() == null || unit.getInputHapp() == null){
                return false;
            }
            ProMemoirs memoirs = new ProMemoirs();
            memoirs.setInputTime(DateLocalUtils.parseGiveStrToDate(unit.getInputTime()));
            memoirs.setInputHapp(unit.getInputHapp());
            memoirs.setUpdateTime(new Date());

            return save(memoirs);

        }catch (Exception e){
            logger.error("插入工程大事记数据发生异常",e);
        }
        return false;
    }

    @Override
    public Boolean deleteByInfo(MemUnit unit) {
        try {

            if (unit == null || unit.getId() == null){
                return false;
            }

            return removeById(unit.getId());

        }catch (Exception e){
            logger.error("删除工程大事记数据发生异常",e);
        }
        return false;
    }

    @Override
    public Boolean updateByInfo(MemUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            ProMemoirs memoirs = new ProMemoirs();
            memoirs.setId(unit.getId());
            if (unit.getInputTime() != null){
                memoirs.setInputTime(DateLocalUtils.parseGiveStrToDate(unit.getInputTime()));
            }

            if (unit.getInputHapp() != null){
                memoirs.setInputHapp(unit.getInputHapp());
            }

            memoirs.setUpdateTime(new Date());

            return updateById(memoirs);

        }catch (Exception e){
            logger.error("更新工程大事记数据发生异常",e);
        }
        return false;
    }

    @Override
    public List<MemUnit> selectAll() {
        try {

            QueryWrapper<ProMemoirs> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("input_time");
            queryWrapper.isNotNull("input_happ");
            queryWrapper.orderByAsc("input_time");

            List<ProMemoirs> list = memMapper.selectList(queryWrapper);
            if (list == null || list.size() == 0){
                return null;
            }

            List<MemUnit> units = new ArrayList<>();
            for (ProMemoirs pm:list) {
                units.add(new MemUnit(pm.getId(),DateLocalUtils.parseDateToStrTwo(pm.getInputTime()),pm.getInputHapp()));
            }
            return units;

        }catch (Exception e){
            logger.error("获取工程大事记数据发生异常",e);
        }
        return null;
    }
}
