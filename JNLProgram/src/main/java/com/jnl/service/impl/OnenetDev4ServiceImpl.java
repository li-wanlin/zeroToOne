package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.OnenetDev4;

import com.jnl.mapper.OnenetDev4Mapper;
import com.jnl.service.OnenetDev4Service;
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
public class OnenetDev4ServiceImpl extends ServiceImpl<OnenetDev4Mapper, OnenetDev4> implements OnenetDev4Service {
    
    
    @Resource
    OnenetDev4Mapper onenetDev4Mapper;

    @Resource
    OverLimitServiceImpl overLimitService;
    

    private static final Logger logger = LoggerFactory.getLogger(OnenetDev4ServiceImpl.class);


    @Override
    public void parseDev4MsgList(List<OnenetParams> pendingDev4List) {

        try{
            if (pendingDev4List.size() == 0){
                return;
            }

            List<OnenetDev4> onenetDev4List = new ArrayList<>(pendingDev4List.size());

            IntStream.range(0, pendingDev4List.size())
                    .parallel()
                    .forEachOrdered(index ->{
                        OnenetParams onenetParams = pendingDev4List.get(index);
                        OnenetDev4 onenetDev4 = new OnenetDev4();
                        onenetDev4.setCsq(onenetParams.getCsq().getValue().intValue());
                        onenetDev4.setVBat(onenetParams.getVbat().getValue());
                        onenetDev4.setTime(onenetParams.getCsq().getTime());
                        onenetDev4.setUpdateTime(DateLocalUtils.parseLongToDate(onenetParams.getCsq().getTime()));
                        if (onenetParams.getRainFall() != null) {
                            onenetDev4.setRainFall(onenetParams.getRainFall().getValue());
                        }
                        if (onenetParams.getLv() != null) {
                            onenetDev4.setLv(onenetParams.getLv().getValue());
                        }
                        if (onenetParams.getLv2() != null) {
                            onenetDev4.setLv2(onenetParams.getLv2().getValue());
                        }
                        if (onenetParams.getLv3() != null) {
                            onenetDev4.setLv3(onenetParams.getLv3().getValue());
                        }
                        if (onenetParams.getLv4() != null) {
                            onenetDev4.setLv4(onenetParams.getLv4().getValue());  //还没接入数据，等接入数据时打开注释
                        }
                        onenetDev4List.add(onenetDev4);
                    });

            if (onenetDev4List.size() == 0){
                return;
            }

            overLimitService.saveDeviceFour(onenetDev4List);

            boolean saveBatch = saveBatch(onenetDev4List);
        }catch (Exception e){
            logger.error("onenet设备4数据存储发生异常",e);
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

            QueryWrapper<OnenetDev4> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            queryWrapper.isNotNull("rainFall");
            List<OnenetDev4> onenetDev4s = onenetDev4Mapper.selectList(queryWrapper);
            if (onenetDev4s == null || onenetDev4s.size() < 1){
                rainUnit.setValue(null);
                return rainUnit;
            }

            return this.generalHour(onenetDev4s,nowHour);
        }catch (Exception e){
            logger.error("获取设备1实时雨量数据发生异常",e);
        }
        rainUnit.setValue(null);
        return null;
    }



    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device4");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev4> dev4Wrapper = new QueryWrapper<>();
            dev4Wrapper.between("update_time",start,now);
            dev4Wrapper.orderByDesc("update_time");
            dev4Wrapper.last("limit 5");

            dev4Wrapper.and(wrapper -> {
                wrapper.isNotNull("lv")
                        .or()
                        .isNotNull("rainFall");
            });

            List<OnenetDev4> devs = onenetDev4Mapper.selectList(dev4Wrapper);

            if (devs != null && devs.size() > 0){
                unit.setRainFall(this.selectRealTimeRain() == null?null:this.selectRealTimeRain().getValue());
                unit.setLv(devs.get(0).getLv());
            }


            QueryWrapper<OnenetDev4> dev402Wrapper = new QueryWrapper<>();
            dev402Wrapper.between("update_time",start,now);
            dev402Wrapper.orderByDesc("update_time");
            dev402Wrapper.last("limit 5");

            dev402Wrapper.and(wrapper -> {
                wrapper.isNotNull("lv2")
                        .or()
                        .isNotNull("lv3");
            });


            List<OnenetDev4> dev02s = onenetDev4Mapper.selectList(dev402Wrapper);


            if (dev02s != null && dev02s.size() > 0){
                unit.setLv2(dev02s.get(0).getLv2());
                unit.setLv3(dev02s.get(0).getLv3());
                unit.setLv4(dev02s.get(0).getLv4());
            }
            return unit;
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


            QueryWrapper<OnenetDev4> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            queryWrapper.isNotNull("rainFall");
            List<OnenetDev4> onenetDev4s = onenetDev4Mapper.selectList(queryWrapper);
            RainUnit nowRain = this.generalHour(onenetDev4s, nowHour);
            RainUnit oneHourRain = this.generalHour(onenetDev4s, oneAgoHour);

            HashSet<RainUnit> rainUnits = new HashSet<>();
            rainUnits.add(nowRain);
            rainUnits.add(oneHourRain);

            return rainUnits;

        }catch (Exception e){
            logger.error("获取设备4最近2小时内雨量数据发生异常",e);
        }
        return null;
    }



    /**
     * 给定指定列表、整点时间，获取该整点时间到下个整点时间内雨量数据
     * @return
     */
    public RainUnit generalHour(List<OnenetDev4> inputList,LocalDateTime inputHour){
        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device4");
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
            List<OnenetDev4> hourDatas = inputList.stream()
                    .filter(onenetDev4 -> onenetDev4.getUpdateTime().after(DateLocalUtils.parseTimeToDate(inputHour)))
                    .filter(onenetDev4 -> onenetDev4.getUpdateTime().before(DateLocalUtils.parseTimeToDate(plusHour)))
                    .sorted(Comparator.comparing(OnenetDev4::getUpdateTime))
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
            List<OnenetDev4> ageDatas = inputList.stream()
                    .filter(onenetDev4 -> onenetDev4.getUpdateTime().after(DateLocalUtils.parseTimeToDate(ageHour)))
                    .filter(onenetDev4 -> onenetDev4.getUpdateTime().before(DateLocalUtils.parseTimeToDate(inputHour)))
                    .sorted(Comparator.comparing(OnenetDev4::getUpdateTime).reversed())
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
            logger.error("获取设备4雨量数据发生异常",e);
        }
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(inputHour));
        rainUnit.setValue(null);
        return rainUnit;
    }
}
