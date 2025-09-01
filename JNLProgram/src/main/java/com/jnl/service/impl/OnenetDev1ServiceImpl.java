package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.OnenetDev1;
import com.jnl.mapper.OnenetDev1Mapper;
import com.jnl.mapper.RainFallDataMapper;
import com.jnl.service.OnenetDev1Service;
import com.jnl.utils.DateLocalUtils;
import com.jnl.utils.PubUtils;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetParams;
import com.jnl.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDev1ServiceImpl extends ServiceImpl<OnenetDev1Mapper, OnenetDev1> implements OnenetDev1Service {


    @Resource
    OnenetDev1Mapper onenetDev1Mapper;


    @Resource
    RainFallDataMapper rainFallDataMapper;


    private static final Logger logger = LoggerFactory.getLogger(OnenetDev1ServiceImpl.class);





    @Async("backgroundTaskExecutor")
    @Override
    public void parseDev1MsgList(List<OnenetParams> pendingDev1List) {
        try{
            if (pendingDev1List.size() == 0){
                return;
            }

            List<OnenetDev1> onenetDev1List = new ArrayList<>(pendingDev1List.size());

            IntStream.range(0, pendingDev1List.size())
                    .parallel()
                    .forEachOrdered(index ->{
                        OnenetParams onenetParams = pendingDev1List.get(index);
                        OnenetDev1 onenetDev1 = new OnenetDev1();
                        onenetDev1.setCsq(onenetParams.getCsq().getValue().intValue());
                        onenetDev1.setVBat(onenetParams.getVbat().getValue());
                        onenetDev1.setTime(onenetParams.getCsq().getTime());
                        onenetDev1.setUpdateTime(DateLocalUtils.parseLongToDate(onenetParams.getCsq().getTime()));
                        if (onenetParams.getRainFall() != null){
                            onenetDev1.setRainFall(onenetParams.getRainFall().getValue());
                        }
                        onenetDev1List.add(onenetDev1);
                    });

            if (onenetDev1List.size() == 0){
                return;
            }


            boolean saveBatch = saveBatch(onenetDev1List);
        }catch (Exception e){
            logger.error("onenet设备1数据存储发生异常",e);
        }

    }


    /**
     * 获取实时雨量，取当前小时内，last-first;若为正值，则返回；若为负值，则返回last；
     * 若当前小时内只有一条数据，则first=上个小时最后一条数据，若上个小时内也没有数据，first=0；
     * 若当前小时内没有数据，返回0
     *
     * 改为
     * 获取实时雨量，取当前小时内，last-first;若为正值，则返回；若为负值，则返回last；
     * 若当前小时内只有一条数据，则first=12小时除当前小时外最后一条数据，若12小时内也没有数据，则返回null；
     * 若当前小时内没有数据，返回null
     * @return
     */
    @Override
    public RainUnit selectRealTimeRain() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime agoHour = nowHour.minusHours(12);

        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device1");
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(nowHour));
        try {

            QueryWrapper<OnenetDev1> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev1> onenetDev1s = onenetDev1Mapper.selectList(queryWrapper);
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
        unit.setDeviceName("device1");

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev1> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");

            List<OnenetDev1> devs = onenetDev1Mapper.selectList(queryWrapper);
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
     * 取最近两个小时雨量数据
     * @return
     */
    public Set<RainUnit> getTwoRainFall(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime oneAgoHour = nowHour.minusHours(1);
            LocalDateTime agoHour = nowHour.minusHours(12);


            QueryWrapper<OnenetDev1> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev1> onenetDev1s = onenetDev1Mapper.selectList(queryWrapper);
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



    /**
     * 给定指定列表、整点时间，获取该整点时间到下个整点时间内雨量数据
     * @return
     */
    public RainUnit generalHour(List<OnenetDev1> inputList,LocalDateTime inputHour){
        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device1");
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
            List<OnenetDev1> hourDatas = inputList.stream()
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().after(DateLocalUtils.parseTimeToDate(inputHour)))
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().before(DateLocalUtils.parseTimeToDate(plusHour)))
                    .sorted(Comparator.comparing(OnenetDev1::getUpdateTime))
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
            List<OnenetDev1> ageDatas = inputList.stream()
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().after(DateLocalUtils.parseTimeToDate(ageHour)))
                    .filter(onenetDev1 -> onenetDev1.getUpdateTime().before(DateLocalUtils.parseTimeToDate(inputHour)))
                    .sorted(Comparator.comparing(OnenetDev1::getUpdateTime).reversed())
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








    public  <T> List<T> tranObjToClass(List<Object> inputList,Class<T> clazz){
        List<T> resultList = new ArrayList<>();
        for (Object obj : inputList) {
            if (clazz.isInstance(obj)) {
                try {
                    T newObj = clazz.getDeclaredConstructor().newInstance();
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        field.set(newObj, field.get(obj));
                    }
                    resultList.add(newObj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return resultList;
    }





}
