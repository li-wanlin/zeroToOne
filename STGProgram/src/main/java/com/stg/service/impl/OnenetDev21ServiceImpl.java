package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.OnenetDev21;
import com.stg.mapper.OnenetDev21Mapper;
import com.stg.service.OnenetDev21Service;
import com.stg.utils.DateLocalUtils;
import com.stg.utils.PubUtils;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;
import com.stg.vo.onenetVo.RainUnit;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDev21ServiceImpl extends ServiceImpl<OnenetDev21Mapper, OnenetDev21> implements OnenetDev21Service {

    @Resource
    OnenetDev21Mapper onenetDev21Mapper;




    private static final Logger logger = LoggerFactory.getLogger(OnenetDev21ServiceImpl.class);



    @Override
    public RainUnit selectRealTimeRain() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime agoHour = nowHour.minusHours(12);

        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device21");
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(nowHour));
        try {

            QueryWrapper<OnenetDev21> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev21> onenetDev1s = onenetDev21Mapper.selectList(queryWrapper);
            if (onenetDev1s == null || onenetDev1s.size() < 1){
                rainUnit.setValue(null);
                return rainUnit;
            }

            return this.generalHour(onenetDev1s,nowHour);
        }catch (Exception e){
            logger.error("获取设备1实时雨量数据发生异常",e);
        }
        rainUnit.setValue(null);
        return null;
    }

    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device21");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev21> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev21> devs = onenetDev21Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                unit.setRainFall(this.selectRealTimeRain() == null?null:this.selectRealTimeRain().getValue());
                return unit;
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return unit;
    }


    /**
     * 给定指定列表、整点时间，获取该整点时间到下个整点时间内雨量数据
     * @return
     */
    public RainUnit generalHour(List<OnenetDev21> inputList, LocalDateTime inputHour){
        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device21");
        Float first;
        Float last;
        try {
            if (inputList == null || inputList.size() < 1){
                rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
                rainUnit.setValue(null);
                return rainUnit;
            }

            LocalDateTime plusHour = inputHour.plusHours(1);
            LocalDateTime ageHour = inputHour.minusHours(12);
            List<OnenetDev21> hourDatas = inputList.stream()
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().after(DateLocalUtils.parseTimeToDate(inputHour)))
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().before(DateLocalUtils.parseTimeToDate(plusHour)))
                    .sorted(Comparator.comparing(OnenetDev21::getUpdateTime))
                    .collect(Collectors.toList());

            //当前小时内没有数据时
            if(hourDatas.size() == 0){
                rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
                rainUnit.setValue(null);
                return rainUnit;
            }


            //当前小时内有2个以上的数据时
            if (hourDatas.size() >= 2){
                first = hourDatas.get(0).getRainFall();
                last = hourDatas.get(hourDatas.size() - 1).getRainFall();
                rainUnit.setValue(PubUtils.keepGiveDecimal(((last - first >= 0)?(last-first):last),1));
                rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
                return rainUnit;
            }

            //当前小时只有一个数据
            List<OnenetDev21> ageDatas = inputList.stream()
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().after(DateLocalUtils.parseTimeToDate(ageHour)))
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().before(DateLocalUtils.parseTimeToDate(inputHour)))
                    .sorted(Comparator.comparing(OnenetDev21::getUpdateTime).reversed())
                    .collect(Collectors.toList());

            //当前小时只有一个数据，且12个小时内有数据时
            if (ageDatas.size() > 0){
                first = ageDatas.get(0).getRainFall();
                last = hourDatas.get(0).getRainFall();
                rainUnit.setValue(PubUtils.keepGiveDecimal(((last - first >= 0)?(last-first):last),1));
                rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
                return rainUnit;
            }


            //当前小时只有一个数据，且12个小时内无数据时,返回null
        }catch (Exception e){
            logger.error("获取设备1雨量数据发生异常",e);
        }
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
        rainUnit.setValue(null);
        return rainUnit;
    }



    /**
     * 取最近两个小时雨量数据
     * @return
     */
    public Set<RainUnit> getTwoRainFall(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime oneAgoHour = nowHour.minusHours(1);
            LocalDateTime agoHour = nowHour.minusHours(12);


            QueryWrapper<OnenetDev21> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev21> onenetDev1s = onenetDev21Mapper.selectList(queryWrapper);
            RainUnit nowRain = this.generalHour(onenetDev1s, nowHour);
            RainUnit oneHourRain = this.generalHour(onenetDev1s, oneAgoHour);

            HashSet<RainUnit> rainUnits = new HashSet<>();
            rainUnits.add(nowRain);
            rainUnits.add(oneHourRain);

            return rainUnits;

        }catch (Exception e){
            logger.error("获取设备1最近2小时内雨量数据发生异常",e);
        }
        return null;
    }





}
