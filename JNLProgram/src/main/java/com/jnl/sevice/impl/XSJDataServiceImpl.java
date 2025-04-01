package com.jnl.sevice.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.XSJData;
import com.jnl.mapper.XSJDataMapper;
import com.jnl.sevice.XSJDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class XSJDataServiceImpl extends ServiceImpl<XSJDataMapper, XSJData> implements XSJDataService {

    @Resource
    XSJDataMapper xsjDataMapper;


    private static final Logger logger = LoggerFactory.getLogger(XSJDataServiceImpl.class);

    @Override
    public XSJData selectDataById(Integer id) {
        try{
            QueryWrapper<XSJData> wrapper = new QueryWrapper<>();
            wrapper.eq("id",id);
            List<XSJData> xsjDataList = xsjDataMapper.selectList(wrapper);


            if (xsjDataList != null && xsjDataList.size() > 0){
                return xsjDataList.get(0);
            }
        }catch (Exception e){
            logger.error("获取新世纪数据出错",e);
        }
        return null;
    }


    @Override
    public Boolean updateByGiveId(Integer id) {
        UpdateWrapper<XSJData> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",id);

        Random random = new Random();
        XSJData xsjData = new XSJData();
        xsjData.setSec(random.nextLong());

        int update = xsjDataMapper.update(xsjData, updateWrapper);
        if (update > 0){
            return true;
        }
        return false;
    }


    @Override
    public Boolean deleteByGiveId(Integer id) {
        QueryWrapper<XSJData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        int delete = xsjDataMapper.delete(queryWrapper);
        if (delete > 0){
            return true;
        }
        return false;
    }


    @Override
    public Boolean insertByGive() {
        XSJData xsjData = new XSJData();
        xsjData.setTime(new Date());
        int insert = xsjDataMapper.insert(xsjData);
        if (insert > 0){
            return true;
        }
        return false;
    }


}
