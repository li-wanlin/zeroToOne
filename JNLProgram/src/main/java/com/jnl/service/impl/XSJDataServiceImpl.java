package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.XSJData;
import com.jnl.mapper.XSJDataMapper;
import com.jnl.service.XSJDataService;
import com.jnl.utils.DateLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class XSJDataServiceImpl extends ServiceImpl<XSJDataMapper, XSJData> implements XSJDataService {

    @Resource
    XSJDataMapper xsjDataMapper;


    private static final Logger logger = LoggerFactory.getLogger(XSJDataServiceImpl.class);


    private static final Map<Double,Double> mapping = new HashMap<>();


    static{
        mapping.put(642.0,0.0);
        mapping.put(645.0,10.0);
        mapping.put(650.0,49.0);
        mapping.put(655.0,141.0);
        mapping.put(660.0,295.0);
        mapping.put(665.0,535.0);
        mapping.put(670.0,887.0);
        mapping.put(675.0,1369.0);
        mapping.put(680.0,2056.0);
        mapping.put(685.0,2864.0);
        mapping.put(690.0,4012.0);
    }


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

    @Override
    public XSJData selectLatestData() {
        try {

            //当日零点时间戳
            //long startMilli = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime now = LocalDateTime.now();


            QueryWrapper<XSJData> wrapper = new QueryWrapper<>();


            //数据库存的是秒，时间戳是毫秒
            //wrapper.gt("sec",startMilli/1000);
            wrapper.between("time",start,now);
            wrapper.orderByDesc("time");


            List<XSJData> xsjDataList = xsjDataMapper.selectList(wrapper);


            if (xsjDataList == null || xsjDataList.size() == 0){
                return null;
            }

            List<XSJData> powerList = xsjDataList.stream()
                    .filter(xsjData -> xsjData.getTime().compareTo(DateLocalUtils.parseTimeToDate(nowHour)) < 0)
                    .collect(Collectors.toList());


            XSJData latestXSJ = xsjDataList.get(0);


            //按小时分组存储总和、数据数量
            HashMap<Integer, HourlyPowerInfo> hourlyPowerMap = new HashMap<>();
            for (XSJData xsjData:powerList) {
                int hour = xsjData.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().getHour();
                HourlyPowerInfo hourlyPowerInfo = hourlyPowerMap.computeIfAbsent(hour, k -> new HourlyPowerInfo());
                hourlyPowerInfo.powerSum += xsjData.getPowerSum();
                hourlyPowerInfo.count++;
            }

            //计算当日累计电量
            Double accrueDouble = 0.0;
            for (HourlyPowerInfo hourlyPowerInfo:hourlyPowerMap.values()) {
                if (hourlyPowerInfo.count > 0){
                    accrueDouble += (hourlyPowerInfo.powerSum/ hourlyPowerInfo.count);
                }
            }

            Double capcityDouble = this.calculateValue(latestXSJ.getStage());

            latestXSJ.setAccrue(accrueDouble.intValue());
            latestXSJ.setCapacity(capcityDouble.intValue());

            return latestXSJ;
        }catch (Exception e){
            logger.error("在获取新世纪实时数据时发生异常",e);
        }
        //说明数据库中没有数据，返回空
        return null;
    }


    private static class HourlyPowerInfo{
        double powerSum = 0;

        int count = 0;
    }


    /**
     * 根据水位计算扩容
     * @param stageInput
     * @return
     */
    public Double calculateValue(Double stageInput){
        try {
            if (stageInput == null){
                return 0.0;
            }

            // 获取所有键并排序
            List<Double> sortedKeys = new ArrayList<>(mapping.keySet());
            Collections.sort(sortedKeys);


            if (stageInput < sortedKeys.get(0)){
                return mapping.get(sortedKeys.get(0));
            }

            if (stageInput > sortedKeys.get(sortedKeys.size() - 1)){
                return mapping.get(sortedKeys.get(sortedKeys.size() - 1));
            }

            Double lowerKey = null;
            Double upperKey = null;
            for (int i = 0; i < sortedKeys.size(); i++) {
                if (Objects.equals(sortedKeys.get(i), stageInput)) {
                    return mapping.get(stageInput);
                }
                if (sortedKeys.get(i) < stageInput) {
                    lowerKey = sortedKeys.get(i);
                } else {
                    upperKey = sortedKeys.get(i);
                    break;
                }
            }

            double lowerValue = mapping.get(lowerKey);
            double upperValue = mapping.get(upperKey);

            // 使用差分公式计算插值结果
           return  ((double) (stageInput - lowerKey) / (upperKey - lowerKey)) * (upperValue - lowerValue) + lowerValue;

        }catch (Exception e){
            logger.error("计算库容时出错",e);
        }
        return null;
    }


}
