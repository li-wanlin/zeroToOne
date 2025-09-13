package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.*;
import com.stg.mapper.*;
import com.stg.service.RainFallDataService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.fourPreventVo.RainRangeUnit;
import com.stg.vo.onenetVo.RainHisUnit;
import com.stg.vo.onenetVo.RainReportUnit;
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
public class RainFallDataServiceImpl extends ServiceImpl<RainFallDataMapper, RainFallData> implements RainFallDataService {

    @Resource
    RainFallDataMapper rainFallDataMapper;


    @Resource
    OnenetDev21ServiceImpl onenetDev21Service;

    @Resource
    OnenetDev22ServiceImpl onenetDev22Service;


    @Resource
    OnenetDev1ServiceImpl onenetDev1Service;

    @Resource
    OnenetDev21Mapper onenetDev21Mapper;

    @Resource
    OnenetDev22Mapper onenetDev22Mapper;

    @Resource
    OnenetDev1Mapper onenetDev1Mapper;



    private static final Logger logger = LoggerFactory.getLogger(RainFallDataServiceImpl.class);




    @Override
    public List<RainUnit> calRain(List<RainFallData> rainFallDatas, Date dayStart, Date dayEnd) {
        List<RainUnit> rainUnits = new ArrayList<>();
        RainUnit rainUnitOne = new RainUnit();
        rainUnitOne.setDevice("device21");
        rainUnitOne.setUpdateTime(dayStart);

        RainUnit rainUnitTwo = new RainUnit();
        rainUnitTwo.setDevice("device22");
        rainUnitTwo.setUpdateTime(dayStart);


        RainUnit rainUnitThree = new RainUnit();
        rainUnitThree.setDevice("device1");
        rainUnitThree.setUpdateTime(dayStart);


        try {
            if (rainFallDatas == null || rainFallDatas.size() == 0){
                return null;
            }

            //双堂沟设备1数据
            List<RainFallData> rainOne = rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getInputTime().after(dayStart))
                    .filter(rainFallData -> rainFallData.getInputTime().before(dayEnd))
                    .filter(rainFallData -> rainFallData.getRainfall1() != null)
                    .collect(Collectors.toList());
            if (rainOne.size() == 0){
                rainUnitOne.setValue(null);
            }else {
                double sum = rainOne.stream()
                        .mapToDouble(RainFallData::getRainfall1)
                        .sum();
                rainUnitOne.setValue((float)sum);
            }


            //双堂沟设备2数据
            List<RainFallData> rainTwo = rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getInputTime().after(dayStart))
                    .filter(rainFallData -> rainFallData.getInputTime().before(dayEnd))
                    .filter(rainFallData -> rainFallData.getRainfall2() != null)
                    .collect(Collectors.toList());
            if (rainTwo.size() == 0){
                rainUnitTwo.setValue(null);
            }else {
                double sum = rainTwo.stream()
                        .mapToDouble(RainFallData::getRainfall2)
                        .sum();
                rainUnitTwo.setValue((float)sum);
            }



            //金牛岭设备1数据
            List<RainFallData> rainThree = rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getInputTime().after(dayStart))
                    .filter(rainFallData -> rainFallData.getInputTime().before(dayEnd))
                    .filter(rainFallData -> rainFallData.getRainfall3() != null)
                    .collect(Collectors.toList());
            if (rainThree.size() == 0){
                rainUnitThree.setValue(null);
            }else {
                double sum = rainThree.stream()
                        .mapToDouble(RainFallData::getRainfall3)
                        .sum();
                rainUnitThree.setValue((float)sum);
            }



            rainUnits.add(rainUnitOne);
            rainUnits.add(rainUnitTwo);
            rainUnits.add(rainUnitThree);


            return rainUnits;
        }catch (Exception e){
            logger.error("获取非当天雨量数据发生异常",e);
        }

        return null;
    }

    @Override
    public void rainFallCall(){
        try {
            LocalDateTime now = LocalDateTime.now();
            QueryWrapper<RainFallData> rainWrapper = new QueryWrapper<>();
            rainWrapper.between("update_time",now.minusMinutes(5),now);
            List<RainFallData> ifHaveData = rainFallDataMapper.selectList(rainWrapper);
            if (ifHaveData != null && ifHaveData.size() > 0){
                return;
            }

            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime agoHour = nowHour.minusHours(72);
            List<RainFallData> rainFallDatas = new ArrayList<>();

            QueryWrapper<OnenetDev21> wrapperOne = new QueryWrapper<>();
            wrapperOne.between("update_time",agoHour,now);


            QueryWrapper<OnenetDev22> wrapperTwo = new QueryWrapper<>();
            wrapperTwo.between("update_time",agoHour,now);

            QueryWrapper<OnenetDev1> wrapperThree = new QueryWrapper<>();
            wrapperTwo.between("update_time",agoHour,now);


            //从数据库中查出3个设备近三天内的数据
            List<OnenetDev21> onenetDev1s = onenetDev21Mapper.selectList(wrapperOne);
            List<OnenetDev22> onenetDev2s = onenetDev22Mapper.selectList(wrapperTwo);
            List<OnenetDev1> onenetDev3s = onenetDev1Mapper.selectList(wrapperThree);


            //开始遍历3个设备近两天每个小时的雨量数据
            for (int i = 0; i < 73; i++) {

                LocalDateTime inputHour = agoHour.plusHours(i);
                LocalDateTime endHour = inputHour.plusHours(1);
                LocalDateTime startHour = inputHour.minusHours(12);
                RainFallData rainFallData = new RainFallData();
                rainFallData.setInputTime(DateLocalUtils.parseTimeToDate(inputHour));

                Integer count = 0;
                Float sum = 0f;


                List<OnenetDev21> devOne = onenetDev1s.stream()
                        .filter(onenetDev21 -> onenetDev21.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev21 -> onenetDev21.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());

                Float valueOne = onenetDev21Service.generalHour(devOne, inputHour).getValue();

                if (valueOne != null){
                    rainFallData.setRainfall1(valueOne);
                    sum = sum + valueOne;
                    count = count + 1;
                }


                List<OnenetDev22> devTwo = onenetDev2s.stream()
                        .filter(onenetDev22 -> onenetDev22.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev22 -> onenetDev22.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());

                Float valueTwo = onenetDev22Service.generalHour(devTwo, inputHour).getValue();

                if (valueTwo != null){
                    rainFallData.setRainfall2(valueTwo);
                    sum = sum + valueTwo;
                    count = count + 1;
                }


                //将石笼沟雨量数据也放入雨量数据中，用作显示，不参与计算和报表
                List<OnenetDev1> devThree = onenetDev3s.stream()
                        .filter(onenetDev1 -> onenetDev1.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev1 -> onenetDev1.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());

                Float valueThree = onenetDev1Service.generalHour(devThree, inputHour).getValue();

                if (valueThree != null){
                    rainFallData.setRainfall3(valueThree);
                }


                if (count > 0){
                    rainFallData.setAverageRain(sum/count);
                }

                rainFallData.setUpdateTime(new Date());

                rainFallDatas.add(rainFallData);

            }



            //boolean saveOrUpdateBatch = rainFallDataService.saveOrUpdateBatch(rainFallDatas);
            Boolean saveOrUpdateBatch = rainFallDataMapper.batchInsertOrUpdate(rainFallDatas);


            if (saveOrUpdateBatch){
                logger.info("每小时雨量定时任务更新成功");
            }else {
                logger.info("每小时雨量定时任务更新失败");
            }


        }catch (Exception e){
            logger.error("每小时雨量定时任务更新发生异常",e);
        }
    }


    @Override
    public List<RainRangeUnit> dayAgo() {
        try {
            List<RainFallData> rainFallDatas;
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime agoHour = nowHour.minusHours(24);

            rainFallDatas = this.selectHourAgo(agoHour,nowHour);

            List<RainFallData> judge = rainFallDatas.stream().filter(rainFallData -> rainFallData.getInputTime().equals(DateLocalUtils.parseTimeToDate(nowHour)))
                    .collect(Collectors.toList());

            if (judge.size() > 0){
                return this.listToRange(rainFallDatas.stream()
                        .filter(rainFallData -> rainFallData.getInputTime().before(DateLocalUtils.parseTimeToDate(nowHour)))
                        .collect(Collectors.toList()));
            }else {
                this.rainFallCall();
                rainFallDatas = this.selectHourAgo(agoHour,nowHour);
                return this.listToRange(rainFallDatas.stream()
                        .filter(rainFallData -> rainFallData.getInputTime().before(DateLocalUtils.parseTimeToDate(nowHour)))
                        .collect(Collectors.toList()));
            }
        }catch (Exception e){
            logger.error("获取最近24小时雨量失败",e);
        }
        return null;
    }


    /**
     * 获取给定时间雨情
     * @param agoHourNum
     * @return
     */
    @Override
    public List<RainHisUnit> dataByGivenHour(Integer agoHourNum) {
        try {
            if (agoHourNum == null || agoHourNum == 0){
                return null;
            }
            List<RainFallData> rainFallDatas;
            LocalDateTime nowHour = LocalDateTime.now().minusHours(1).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime agoHour = nowHour.minusHours(agoHourNum-1);

            rainFallDatas = this.selectHourAgo(agoHour,nowHour);

            List<RainFallData> judge = rainFallDatas.stream().filter(rainFallData -> rainFallData.getInputTime().equals(DateLocalUtils.parseTimeToDate(nowHour)))
                    .collect(Collectors.toList());


            List<RainHisUnit> rainHisUnits = new ArrayList<>();

            RainHisUnit hisOne = new RainHisUnit();
            hisOne.setDeviceName("水库上游");

            RainHisUnit hisTwo = new RainHisUnit();
            hisTwo.setDeviceName("大坝");

            RainHisUnit hisThree = new RainHisUnit();
            hisThree.setDeviceName("石笼沟");


            List<RainRangeUnit> listOne = new ArrayList<>();
            List<RainRangeUnit> listTwo = new ArrayList<>();
            List<RainRangeUnit> listThree = new ArrayList<>();




            if (judge.size() > 0){
                for (RainFallData rainFall:rainFallDatas) {
                    listOne.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall1()==null?null:(double)rainFall.getRainfall1()));
                    listTwo.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall2()==null?null:(double)rainFall.getRainfall2()));
                    listThree.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall3()==null?null:(double)rainFall.getRainfall3()));
                }


                hisOne.setRainUnits(listOne);
                hisOne.setSumRain(listOne.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisOne.setMaxRain(listOne.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));


                hisTwo.setRainUnits(listTwo);
                hisTwo.setSumRain(listTwo.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisTwo.setMaxRain(listTwo.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));

                hisThree.setRainUnits(listThree);
                hisThree.setSumRain(listThree.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisThree.setMaxRain(listThree.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));



                rainHisUnits.add(hisOne);
                rainHisUnits.add(hisTwo);
                rainHisUnits.add(hisThree);

                return rainHisUnits;

            }else {
                this.rainFallCall();
                rainFallDatas = this.selectHourAgo(agoHour,nowHour);

                for (RainFallData rainFall:rainFallDatas) {
                    listOne.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall1()==null?null:(double)rainFall.getRainfall1()));
                    listTwo.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall2()==null?null:(double)rainFall.getRainfall2()));
                    listThree.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall3()==null?null:(double)rainFall.getRainfall3()));
                }


                hisOne.setRainUnits(listOne);
                hisOne.setSumRain(listOne.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisOne.setMaxRain(listOne.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));


                hisTwo.setRainUnits(listTwo);
                hisTwo.setSumRain(listTwo.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisTwo.setMaxRain(listTwo.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));

                hisThree.setRainUnits(listThree);
                hisThree.setSumRain(listThree.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisThree.setMaxRain(listThree.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));


                rainHisUnits.add(hisOne);
                rainHisUnits.add(hisTwo);
                rainHisUnits.add(hisThree);

                return rainHisUnits;


            }
        }catch (Exception e){
            logger.error("获取雨量失败",e);
        }
        return null;
    }

    @Override
    public List<RainReportUnit> selectRainReport(String dateStr) {
        try {
            if (dateStr == null || dateStr.length() < 10){
                return null;
            }

            this.rainFallCall();

            Date end = DateLocalUtils.parseStrToEnd(dateStr);
            Date start = DateLocalUtils.parseStrToStart(dateStr);
            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",start,end);
            queryWrapper.orderByAsc("input_time");
            List<RainFallData> rains = rainFallDataMapper.selectList(queryWrapper);
            if (rains == null || rains.size() == 0){
                return null;
            }


            List<RainReportUnit> units = new ArrayList<>();
            for (RainFallData rain:rains) {
                RainReportUnit unit = new RainReportUnit();
                unit.setInputTime(DateLocalUtils.parseDateToStr(rain.getInputTime()));
                unit.setRainFall1(rain.getRainfall1()==null?null:(double)rain.getRainfall1());
                unit.setRainFall2(rain.getRainfall2()==null?null:(double)rain.getRainfall2());

                units.add(unit);
            }


            return units;
        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


    public Map<String,Double> rainFallAverage(){
        try {
            List<RainFallData> rainFallDatas;
            LocalDateTime nowDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime oneDay = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime twoDay = LocalDateTime.now().minusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0);

            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time", twoDay, nowDay);
            queryWrapper.orderByAsc("input_time");

            rainFallDatas = rainFallDataMapper.selectList(queryWrapper);
            if (rainFallDatas == null || rainFallDatas.size() < 3){
                return null;
            }

            double twoDayRain = rainFallDatas.stream().filter(rainFallData -> rainFallData.getInputTime().after(DateLocalUtils.parseTimeToDate(twoDay)))
                    .filter(rainFallData -> rainFallData.getInputTime().before(DateLocalUtils.parseTimeToDate(oneDay)))
                    .filter(rainFallData -> rainFallData.getAverageRain() != null)
                    .mapToDouble(RainFallData::getAverageRain)
                    .sum();

            double oneDayRain = rainFallDatas.stream().filter(rainFallData -> rainFallData.getInputTime().after(DateLocalUtils.parseTimeToDate(oneDay)))
                    .filter(rainFallData -> rainFallData.getInputTime().before(DateLocalUtils.parseTimeToDate(nowDay)))
                    .filter(rainFallData -> rainFallData.getAverageRain() != null)
                    .mapToDouble(RainFallData::getAverageRain)
                    .sum();

            Map<String, Double> map = new HashMap<>();
            map.put("twoDayRain",twoDayRain);
            map.put("oneDayRain",oneDayRain);

            return map;

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


    /**
     * 获取给定时间内的数据
     * @param agoHour
     * @param nowHour
     * @return
     */
    public List<RainFallData> selectHourAgo(LocalDateTime agoHour,LocalDateTime nowHour){
        try {

            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();

            queryWrapper.between("input_time",agoHour,nowHour);
            queryWrapper.orderByAsc("input_time");
            List<RainFallData> rainFallDatas = rainFallDataMapper.selectList(queryWrapper);
            if (rainFallDatas == null || rainFallDatas.size() == 0){
                return null;
            }
            return rainFallDatas;
        }catch (Exception e){
            logger.error("获取给定时间数据失败",e);
        }
        return null;
    }





    public List<RainRangeUnit> listToRange(List<RainFallData> rainFallDatas){
        try {
            if (rainFallDatas == null || rainFallDatas.size() == 0){
                return null;
            }

            List<RainRangeUnit> units = new ArrayList<>();
            for (RainFallData ra:rainFallDatas) {

                RainRangeUnit unit = new RainRangeUnit();
                unit.setRainTime(DateLocalUtils.parseDateToStr(ra.getInputTime()));
                unit.setValue(ra.getAverageRain()== null?null:(double)ra.getAverageRain());

                units.add(unit);
            }

            return units;
        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


    public Double calDiff(List<RainFallData> rainFallDatas){
        try {
            List<RainFallData> fallData = rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getAverageRain() != null)
                    .collect(Collectors.toList());

            if (fallData.size() == 0){
                return null;
            }


            rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getAverageRain() == null)
                    .forEach(rainFallData -> rainFallData.setAverageRain(0f));


            double diffSum = IntStream.range(0, rainFallDatas.size() - 1)
                    .mapToDouble(i -> {
                        RainFallData rainPre = rainFallDatas.get(i);
                        RainFallData rainSuf = rainFallDatas.get(i + 1);
                        return (rainSuf.getAverageRain() - rainPre.getAverageRain()) >= 0
                                ? (rainSuf.getAverageRain() - rainPre.getAverageRain()) : rainSuf.getAverageRain();
                    })
                    .sum();
            return diffSum;
        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


}
