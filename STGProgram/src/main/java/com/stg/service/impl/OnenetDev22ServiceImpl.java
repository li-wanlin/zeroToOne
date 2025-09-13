package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.OnenetDev21;
import com.stg.entity.OnenetDev22;
import com.stg.mapper.OnenetDev22Mapper;
import com.stg.service.OnenetDev22Service;
import com.stg.utils.DateLocalUtils;
import com.stg.utils.PubUtils;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.onenetVo.CapacityResponse;
import com.stg.vo.onenetVo.DevUnit;
import com.stg.vo.onenetVo.OnenetParams;
import com.stg.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OnenetDev22ServiceImpl extends ServiceImpl<OnenetDev22Mapper, OnenetDev22> implements OnenetDev22Service {


    @Resource
    OverLimitServiceImpl overLimitService;


    @Resource
    OnenetDev22Mapper onenetDev22Mapper;

    private static final Logger logger = LoggerFactory.getLogger(OnenetDev22ServiceImpl.class);


    private static final Map<Double,Double> mapping = new HashMap<>();


    static{
        mapping.put(900.0,0.0);
        mapping.put(907.56,8.9);
        mapping.put(940.0,130.59);
        mapping.put(942.26,148.47);
        mapping.put(943.08,152.83);
    }



    @Override
    public RainUnit selectRealTimeRain() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime agoHour = nowHour.minusHours(12);

        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device22");
        rainUnit.setUpdateTime(DateLocalUtils.parseTimeToDate(nowHour));
        try {

            QueryWrapper<OnenetDev22> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev22> onenetDev2s = onenetDev22Mapper.selectList(queryWrapper);
            if (onenetDev2s == null || onenetDev2s.size() < 1){
                rainUnit.setValue(null);
                return rainUnit;
            }

            return this.generalHour(onenetDev2s,nowHour);
        }catch (Exception e){
            logger.error("获取设备1实时雨量数据发生异常",e);
        }
        rainUnit.setValue(null);
        return null;
    }

    @Override
    public DevUnit selectLatest() {
        DevUnit unit = new DevUnit();
        unit.setDeviceName("device22");
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusHours(12);
            QueryWrapper<OnenetDev22> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.le("lv",944);
            queryWrapper.last("limit 5");

            List<OnenetDev22> devs = onenetDev22Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                unit.setLv(devs.get(0).getLv());
                unit.setRainFall(this.selectRealTimeRain() == null?null:this.selectRealTimeRain().getValue());
                return unit;
            }
        }catch (Exception e){
            logger.error("获取实时数据失败",e);
        }
        return unit;
    }

    @Override
    public CapacityResponse selectCapaInfo() {
        CapacityResponse response = new CapacityResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusHours(12);
            QueryWrapper<OnenetDev22> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.le("lv",944);
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 5");
            List<OnenetDev22> devs = onenetDev22Mapper.selectList(queryWrapper);
            if (devs != null && devs.size() > 0){
                response.setUpdateTime(DateLocalUtils.parseDateToStr(devs.get(0).getUpdateTime()));
                response.setLv((double)devs.get(0).getLv());
                response.setCapacity(this.calculateValue((double)devs.get(0).getLv()));
                meta.setMsg("获取库容水位信息成功");
                meta.setStatus(200);
                return response;
            }
        }catch (Exception e){
            logger.error("获取库容水位信息发生异常",e);
        }
        meta.setMsg("获取库容水位信息失败");
        meta.setStatus(400);
        return response;
    }


    /**
     * 给定指定列表、整点时间，获取该整点时间到下个整点时间内雨量数据
     * @return
     */
    public RainUnit generalHour(List<OnenetDev22> inputList, LocalDateTime inputHour){
        RainUnit rainUnit = new RainUnit();
        rainUnit.setDevice("device22");
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
            List<OnenetDev22> hourDatas = inputList.stream()
                    .filter(OnenetDev22 -> OnenetDev22.getUpdateTime().after(DateLocalUtils.parseTimeToDate(inputHour)))
                    .filter(OnenetDev22 -> OnenetDev22.getUpdateTime().before(DateLocalUtils.parseTimeToDate(plusHour)))
                    .sorted(Comparator.comparing(OnenetDev22::getUpdateTime))
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
            List<OnenetDev22> ageDatas = inputList.stream()
                    .filter(OnenetDev22 -> OnenetDev22.getUpdateTime().after(DateLocalUtils.parseTimeToDate(ageHour)))
                    .filter(OnenetDev22 -> OnenetDev22.getUpdateTime().before(DateLocalUtils.parseTimeToDate(inputHour)))
                    .sorted(Comparator.comparing(OnenetDev22::getUpdateTime).reversed())
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


    public Set<RainUnit> getTwoRainFall(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime oneAgoHour = nowHour.minusHours(1);
            LocalDateTime agoHour = nowHour.minusHours(12);


            QueryWrapper<OnenetDev22> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",agoHour,now);
            List<OnenetDev22> onenetDev2s = onenetDev22Mapper.selectList(queryWrapper);
            RainUnit nowRain = this.generalHour(onenetDev2s, nowHour);
            RainUnit oneHourRain = this.generalHour(onenetDev2s, oneAgoHour);

            HashSet<RainUnit> rainUnits = new HashSet<>();
            rainUnits.add(nowRain);
            rainUnits.add(oneHourRain);

            return rainUnits;

        }catch (Exception e){
            logger.error("获取设备2最近2小时内雨量数据发生异常",e);
        }
        return null;
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
