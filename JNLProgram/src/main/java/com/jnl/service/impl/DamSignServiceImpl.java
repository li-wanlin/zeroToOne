package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.DamSign;
import com.jnl.mapper.DamSignMapper;
import com.jnl.service.DamSignService;
import com.jnl.vo.damSignVo.SignUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class DamSignServiceImpl extends ServiceImpl<DamSignMapper, DamSign> implements DamSignService {

    @Resource
    DamSignMapper damSignMapper;

    private static final Logger logger = LoggerFactory.getLogger(DamSignServiceImpl.class);


    @Override
    public Boolean insertByInfo(SignUnit unit) {
        try {
            if (unit == null || StringUtils.isAnyEmpty(unit.getSign())){
                return false;
            }
            DamSign damSign = new DamSign();
            damSign.setDateTime(new Date());
            damSign.setSign(unit.getSign());

            return save(damSign);


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



    @Override
    public Boolean deleteByInfo(SignUnit unit) {
        try {
            if (unit == null || unit.getId() == null){
                return false;
            }

            return removeById(unit.getId());


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



    @Override
    public Boolean updateByInfo(SignUnit unit) {
        try {
            if (unit == null || unit.getId() == null || unit.getSign() == null){
                return false;
            }
            DamSign damSign = new DamSign();
            damSign.setId(unit.getId());
            damSign.setDateTime(new Date());
            damSign.setSign(unit.getSign());



            return updateById(damSign);


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }



    @Override
    public SignUnit selectEnable() {
        try {

            QueryWrapper<DamSign> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("sign");
            queryWrapper.orderByDesc("date_time");


            List<DamSign> damSigns = damSignMapper.selectList(queryWrapper);
            if (damSigns != null || damSigns.size() > 0){
               return new SignUnit(damSigns.get(0).getId(),damSigns.get(0).getSign());
            }

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }

}
