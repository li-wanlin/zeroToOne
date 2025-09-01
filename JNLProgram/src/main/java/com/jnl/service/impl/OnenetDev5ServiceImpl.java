package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.OnenetDev5;
import com.jnl.mapper.OnenetDev5Mapper;
import com.jnl.service.OnenetDev5Service;
import com.jnl.utils.DateLocalUtils;
import com.jnl.utils.PubUtils;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDev5ServiceImpl extends ServiceImpl<OnenetDev5Mapper, OnenetDev5> implements OnenetDev5Service {
    
    
    @Resource
    OnenetDev5Mapper onenetDev5Mapper;

    @Resource
    OverLimitServiceImpl overLimitService;
    


    private static final Logger logger = LoggerFactory.getLogger(OnenetDev5ServiceImpl.class);



    @Override
    public Boolean parseMsg(List<OnenetDev5> onenetDev5List) {
        return saveBatch(onenetDev5List);
    }


    @Override
    public void parseDev5MsgList(List<OnenetParams> pendingDev5List) {

        try{
            if (pendingDev5List.size() == 0){
                return;
            }

            List<OnenetDev5> onenetDev5List = new ArrayList<>(pendingDev5List.size());

            IntStream.range(0, pendingDev5List.size())
                    .parallel()
                    .forEachOrdered(index ->{
                        OnenetParams onenetParams = pendingDev5List.get(index);
                        OnenetDev5 onenetDev5 = new OnenetDev5();
                        onenetDev5.setCsq(onenetParams.getCsq().getValue().intValue());
                        onenetDev5.setVBat(onenetParams.getVbat().getValue());
                        onenetDev5.setTime(onenetParams.getCsq().getTime());
                        onenetDev5.setUpdateTime(DateLocalUtils.parseLongToDate(onenetParams.getCsq().getTime()));
                        if (onenetParams.getRainFall() != null) {
                            onenetDev5.setRainFall(onenetParams.getRainFall().getValue());
                        }
                        if (onenetParams.getLv() != null) {
                            onenetDev5.setLv(onenetParams.getLv().getValue());
                        }
                        onenetDev5List.add(onenetDev5);
                    });

            if (onenetDev5List.size() == 0){
                return;
            }

            overLimitService.saveDeviceFive(onenetDev5List);

            boolean saveBatch = saveBatch(onenetDev5List);
        }catch (Exception e){
            logger.error("onenet设备5数据存储发生异常",e);
        }


    }

    @Override
    public RainUnit selectRealTimeRain() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime agoHour = nowHour.minusHours(12);

        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device1");
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(nowHour));
        try {

            QueryWrapper<OnenetDev5> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev5> onenetDev5s = onenetDev5Mapper.selectList(queryWrapper);
            if (onenetDev5s == null || onenetDev5s.size() < 1){
                rainUnit.setValue(null);
                return rainUnit;
            }

            return this.generalHour(onenetDev5s,nowHour);
        }catch (Exception e){
            logger.error("获取设备1实时雨量数据发生异常",e);
        }
        rainUnit.setValue(null);
        return null;
    }




    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device5");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev5> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev5> devs = onenetDev5Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                unit.setRainFall(this.selectRealTimeRain() == null?null:this.selectRealTimeRain().getValue());
                unit.setLv(devs.get(0).getLv());
                return unit;
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return unit;
    }


    public Set<RainUnit> getTwoRainFall(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime oneAgoHour = nowHour.minusHours(1);
            LocalDateTime agoHour = nowHour.minusHours(12);


            QueryWrapper<OnenetDev5> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev5> onenetDev5s = onenetDev5Mapper.selectList(queryWrapper);
            RainUnit nowRain = this.generalHour(onenetDev5s, nowHour);
            RainUnit oneHourRain = this.generalHour(onenetDev5s, oneAgoHour);

            HashSet<RainUnit> rainUnits = new HashSet<>();
            rainUnits.add(nowRain);
            rainUnits.add(oneHourRain);

            return rainUnits;

        }catch (Exception e){
            logger.error("获取设备5最近2小时内雨量数据发生异常",e);
        }
        return null;
    }



    /**
     * 给定指定列表、整点时间，获取该整点时间到下个整点时间内雨量数据
     * @return
     */
    public RainUnit generalHour(List<OnenetDev5> inputList,LocalDateTime inputHour){
        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device5");
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
            List<OnenetDev5> hourDatas = inputList.stream()
                    .filter(onenetDev5 -> onenetDev5.getUpdateTime().after(DateLocalUtils.parseTimeToDate(inputHour)))
                    .filter(onenetDev5 -> onenetDev5.getUpdateTime().before(DateLocalUtils.parseTimeToDate(plusHour)))
                    .sorted(Comparator.comparing(OnenetDev5::getUpdateTime))
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
            List<OnenetDev5> ageDatas = inputList.stream()
                    .filter(onenetDev5 -> onenetDev5.getUpdateTime().after(DateLocalUtils.parseTimeToDate(ageHour)))
                    .filter(onenetDev5 -> onenetDev5.getUpdateTime().before(DateLocalUtils.parseTimeToDate(inputHour)))
                    .sorted(Comparator.comparing(OnenetDev5::getUpdateTime).reversed())
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
            logger.error("获取设备5雨量数据发生异常",e);
        }
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
        rainUnit.setValue(null);
        return rainUnit;
    }
    
    
}
