package com.jnl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.*;
import com.jnl.mapper.*;
import com.jnl.service.RainFallDataService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.fourPreventVo.RainRangeUnit;
import com.jnl.vo.onenetVo.RainHisUnit;
import com.jnl.vo.onenetVo.RainReportUnit;
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
public class RainFallDataServiceImpl extends ServiceImpl<RainFallDataMapper, RainFallData> implements RainFallDataService {

    @Resource
    RainFallDataMapper rainFallDataMapper;




    @Resource
    OnenetDev1ServiceImpl onenetDev1Service;


    @Resource
    OnenetDev2ServiceImpl onenetDev2Service;


    @Resource
    OnenetDev3ServiceImpl onenetDev3Service;


    @Resource
    OnenetDev4ServiceImpl onenetDev4Service;


    @Resource
    OnenetDev5ServiceImpl onenetDev5Service;


    @Resource
    OnenetDev1Mapper onenetDev1Mapper;

    @Resource
    OnenetDev2Mapper onenetDev2Mapper;

    @Resource
    OnenetDev3Mapper onenetDev3Mapper;

    @Resource
    OnenetDev4Mapper onenetDev4Mapper;

    @Resource
    OnenetDev5Mapper onenetDev5Mapper;




    private static final Logger logger = LoggerFactory.getLogger(RainFallDataServiceImpl.class);




    @Override
    public List<RainUnit> calRain(List<RainFallData> rainFallDatas, Date dayStart, Date dayEnd) {
        List<RainUnit> rainUnits = new ArrayList<>();
        RainUnit rainUnitOne = new RainUnit();
        rainUnitOne.setDevice("device1");
        rainUnitOne.setUpdateTime(dayStart);

        RainUnit rainUnitTwo = new RainUnit();
        rainUnitTwo.setDevice("device2");
        rainUnitTwo.setUpdateTime(dayStart);

        RainUnit rainUnitThree = new RainUnit();
        rainUnitThree.setDevice("device3");
        rainUnitThree.setUpdateTime(dayStart);

        RainUnit rainUnitFour = new RainUnit();
        rainUnitFour.setDevice("device4");
        rainUnitFour.setUpdateTime(dayStart);

        RainUnit rainUnitFive = new RainUnit();
        rainUnitFive.setDevice("device5");
        rainUnitFive.setUpdateTime(dayStart);

        try {
            if (rainFallDatas == null || rainFallDatas.size() == 0){
                return null;
            }

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


            List<RainFallData> rainFour = rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getInputTime().after(dayStart))
                    .filter(rainFallData -> rainFallData.getInputTime().before(dayEnd))
                    .filter(rainFallData -> rainFallData.getRainfall4() != null)
                    .collect(Collectors.toList());
            if (rainFour.size() == 0){
                rainUnitFour.setValue(null);
            }else {
                double sum = rainFour.stream()
                        .mapToDouble(RainFallData::getRainfall4)
                        .sum();
                rainUnitFour.setValue((float)sum);
            }



            List<RainFallData> rainFive = rainFallDatas.stream()
                    .filter(rainFallData -> rainFallData.getInputTime().after(dayStart))
                    .filter(rainFallData -> rainFallData.getInputTime().before(dayEnd))
                    .filter(rainFallData -> rainFallData.getRainfall5() != null)
                    .collect(Collectors.toList());
            if (rainFive.size() == 0){
                rainUnitFive.setValue(null);
            }else {
                double sum = rainFive.stream()
                        .mapToDouble(RainFallData::getRainfall5)
                        .sum();
                rainUnitFive.setValue((float)sum);
            }

            rainUnits.add(rainUnitOne);
            rainUnits.add(rainUnitTwo);
            rainUnits.add(rainUnitThree);
            rainUnits.add(rainUnitFour);
            rainUnits.add(rainUnitFive);


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

            QueryWrapper<OnenetDev1> wrapperOne = new QueryWrapper<>();
            wrapperOne.between("update_time",agoHour,now);


            QueryWrapper<OnenetDev2> wrapperTwo = new QueryWrapper<>();
            wrapperTwo.between("update_time",agoHour,now);


            QueryWrapper<OnenetDev3> wrapperThree = new QueryWrapper<OnenetDev3>();
            wrapperThree.between("update_time",agoHour,now);


            QueryWrapper<OnenetDev4> wrapperFour = new QueryWrapper<>();
            wrapperFour.between("update_time",agoHour,now);
            wrapperFour.isNotNull("rainFall");


            QueryWrapper<OnenetDev5> wrapperFive = new QueryWrapper<>();
            wrapperFive.between("update_time",agoHour,now);

            //从数据库中查出5个设备近三天内的数据
            List<OnenetDev1> onenetDev1s = onenetDev1Mapper.selectList(wrapperOne);
            List<OnenetDev2> onenetDev2s = onenetDev2Mapper.selectList(wrapperTwo);
            List<OnenetDev3> onenetDev3s = onenetDev3Mapper.selectList(wrapperThree);
            List<OnenetDev4> onenetDev4s = onenetDev4Mapper.selectList(wrapperFour);
            List<OnenetDev5> onenetDev5s = onenetDev5Mapper.selectList(wrapperFive);


            //开始遍历5个设备近两天每个小时的雨量数据
            for (int i = 0; i < 73; i++) {

                LocalDateTime inputHour = agoHour.plusHours(i);
                LocalDateTime endHour = inputHour.plusHours(1);
                LocalDateTime startHour = inputHour.minusHours(12);
                RainFallData rainFallData = new RainFallData();
                rainFallData.setInputTime(DateLocalUtils.parseTimeToDate(inputHour));

                Integer count = 0;
                Float sum = 0f;


                List<OnenetDev1> devOne = onenetDev1s.stream()
                        .filter(onenetDev1 -> onenetDev1.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev1 -> onenetDev1.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());

                Float valueOne = onenetDev1Service.generalHour(devOne, inputHour).getValue();

                if (valueOne != null){
                    rainFallData.setRainfall1(valueOne);
                    sum = sum + valueOne;
                    count = count + 1;
                }


                List<OnenetDev2> devTwo = onenetDev2s.stream()
                        .filter(onenetDev2 -> onenetDev2.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev2 -> onenetDev2.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());

                Float valueTwo = onenetDev2Service.generalHour(devTwo, inputHour).getValue();

                if (valueTwo != null){
                    rainFallData.setRainfall2(valueTwo);
                    sum = sum + valueTwo;
                    count = count + 1;
                }


                List<OnenetDev3> devThree = onenetDev3s.stream()
                        .filter(onenetDev3 -> onenetDev3.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev3 -> onenetDev3.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());


                Float valueThree = onenetDev3Service.generalHour(devThree, inputHour).getValue();

                if (valueThree != null){
                    rainFallData.setRainfall3(valueThree);
                    sum = sum + valueThree;
                    count = count + 1;
                }


                List<OnenetDev4> devFour = onenetDev4s.stream()
                        .filter(onenetDev4 -> onenetDev4.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev4 -> onenetDev4.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());


                Float valueFour = onenetDev4Service.generalHour(devFour, inputHour).getValue();

                if (valueFour != null){
                    rainFallData.setRainfall4(valueFour);
                    sum = sum + valueFour;
                    count = count + 1;
                }


                List<OnenetDev5> devFive = onenetDev5s.stream()
                        .filter(onenetDev5 -> onenetDev5.getUpdateTime().before(DateLocalUtils.parseTimeToDate(endHour)))
                        .filter(onenetDev5 -> onenetDev5.getUpdateTime().after(DateLocalUtils.parseTimeToDate(startHour)))
                        .collect(Collectors.toList());


                Float valueFive = onenetDev5Service.generalHour(devFive, inputHour).getValue();


                if (valueFive != null){
                    rainFallData.setRainfall5(valueFive);
                    sum = sum + valueFive;
                    count = count + 1;
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
            hisOne.setDeviceName("石笼沟");

            RainHisUnit hisTwo = new RainHisUnit();
            hisTwo.setDeviceName("城区");

            RainHisUnit hisThree = new RainHisUnit();
            hisThree.setDeviceName("洪洛河");

            RainHisUnit hisFour = new RainHisUnit();
            hisFour.setDeviceName("两河口");

            RainHisUnit hisFive = new RainHisUnit();
            hisFive.setDeviceName("金牛岭大坝");

            List<RainRangeUnit> listOne = new ArrayList<>();
            List<RainRangeUnit> listTwo = new ArrayList<>();
            List<RainRangeUnit> listThree = new ArrayList<>();
            List<RainRangeUnit> listFour = new ArrayList<>();
            List<RainRangeUnit> listFive = new ArrayList<>();



            if (judge.size() > 0){
                for (RainFallData rainFall:rainFallDatas) {
                    listOne.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall1()==null?null:(double)rainFall.getRainfall1()));
                    listTwo.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall2()==null?null:(double)rainFall.getRainfall2()));
                    listThree.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall3()==null?null:(double)rainFall.getRainfall3()));
                    listFour.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall4()==null?null:(double)rainFall.getRainfall4()));
                    listFive.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall5()==null?null:(double)rainFall.getRainfall5()));
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

                hisFour.setRainUnits(listFour);
                hisFour.setSumRain(listFour.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisFour.setMaxRain(listFour.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));

                hisFive.setRainUnits(listFive);
                hisFive.setSumRain(listFive.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisFive.setMaxRain(listFive.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));

                rainHisUnits.add(hisOne);
                rainHisUnits.add(hisTwo);
                rainHisUnits.add(hisThree);
                rainHisUnits.add(hisFour);
                rainHisUnits.add(hisFive);

                return rainHisUnits;


/*                return this.listToRange(rainFallDatas.stream()
                        .filter(rainFallData -> rainFallData.getInputTime().before(DateLocalUtils.parseTimeToDate(nowHour)))
                        .collect(Collectors.toList()));*/
            }else {
                this.rainFallCall();
                rainFallDatas = this.selectHourAgo(agoHour,nowHour);

                for (RainFallData rainFall:rainFallDatas) {
                    listOne.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall1()==null?null:(double)rainFall.getRainfall1()));
                    listTwo.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall2()==null?null:(double)rainFall.getRainfall2()));
                    listThree.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall3()==null?null:(double)rainFall.getRainfall3()));
                    listFour.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall4()==null?null:(double)rainFall.getRainfall4()));
                    listFive.add(new RainRangeUnit(DateLocalUtils.parseDateToStr(rainFall.getInputTime()),rainFall.getRainfall5()==null?null:(double)rainFall.getRainfall5()));
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

                hisFour.setRainUnits(listFour);
                hisFour.setSumRain(listFour.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisFour.setMaxRain(listFour.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));

                hisFive.setRainUnits(listFive);
                hisFive.setSumRain(listFive.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
                hisFive.setMaxRain(listFive.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).max().orElse(0.0));

                rainHisUnits.add(hisOne);
                rainHisUnits.add(hisTwo);
                rainHisUnits.add(hisThree);
                rainHisUnits.add(hisFour);
                rainHisUnits.add(hisFive);

                return rainHisUnits;



/*                return this.listToRange(rainFallDatas.stream()
                        .filter(rainFallData -> rainFallData.getInputTime().before(DateLocalUtils.parseTimeToDate(nowHour)))
                        .collect(Collectors.toList()));*/
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
                unit.setRainFall3(rain.getRainfall3()==null?null:(double)rain.getRainfall3());
                unit.setRainFall4(rain.getRainfall4()==null?null:(double)rain.getRainfall4());
                unit.setRainFall5(rain.getRainfall5()==null?null:(double)rain.getRainfall5());

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
