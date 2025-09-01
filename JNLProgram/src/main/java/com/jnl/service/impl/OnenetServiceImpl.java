package com.jnl.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jnl.entity.RainFallData;
import com.jnl.entity.RealTimeData;
import com.jnl.mapper.RainFallDataMapper;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.onenetVo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OnenetServiceImpl {


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
    OnenetDev6ServiceImpl onenetDev6Service;


    @Resource
    OnenetDev7ServiceImpl onenetDev7Service;



    @Resource
    RainFallDataServiceImpl rainFallDataService;


    @Resource
    RealTimeDataServiceImpl realTimeDataService;


    @Resource
    RainFallDataMapper rainFallDataMapper;






    private static final Logger logger = LoggerFactory.getLogger(OnenetServiceImpl.class);


    /**
     * 获取每个雨量计整点到现在的数据
     * @return
     */
    public List<RainUnit> selectRealTimeRain(){
        List<RainUnit> rainUnits = new ArrayList<>();

        RealTimeData realTime = realTimeDataService.getById(1);

        if (realTime == null){
            return null;
        }
        rainUnits.add(new RainUnit("device1",realTime.getInputTime(), realTime.getRainfall1()));
        rainUnits.add(new RainUnit("device2",realTime.getInputTime(), realTime.getRainfall2()));
        rainUnits.add(new RainUnit("device3",realTime.getInputTime(), realTime.getRainfall3()));
        rainUnits.add(new RainUnit("device4",realTime.getInputTime(), realTime.getRainfall4()));
        rainUnits.add(new RainUnit("device5",realTime.getInputTime(), realTime.getRainfall5()));

        return rainUnits;
    }


    /**
     *
     * @return
     */
    public List<DevUnit> selectLatest(){
        ArrayList<DevUnit> devUnits = new ArrayList<>();
        try {

            RealTimeData realTime = realTimeDataService.getById(1);
            if (realTime == null){
                return null;
            }

            DevUnit devUnitOne = onenetDev1Service.selectLatest();
            DevUnit devUnitTwo = onenetDev2Service.selectLatest();
            DevUnit devUnitThree = onenetDev3Service.selectLatest();
            DevUnit devUnitFour = onenetDev4Service.selectLatest();
            DevUnit devUnitFive = onenetDev5Service.selectLatest();

            devUnitOne.setRainFall(realTime.getRainfall1());
            devUnitTwo.setRainFall(realTime.getRainfall2());
            devUnitThree.setRainFall(realTime.getRainfall3());
            devUnitFour.setRainFall(realTime.getRainfall4());
            devUnitFive.setRainFall(realTime.getRainfall5());



            devUnits.add(devUnitOne);
            devUnits.add(devUnitTwo);
            devUnits.add(devUnitThree);
            devUnits.add(devUnitFour);
            devUnits.add(devUnitFive);
            devUnits.add(onenetDev6Service.selectLatest());
            devUnits.add(onenetDev7Service.selectLatest());


            return devUnits;

        }catch (Exception e){
            logger.error("获取onenet设备实时数据失败",e);
        }
        return null;
    }



    /**
     * 获取客户端传送日期的整天雨量数据
     * @return
     */
    public List<RainUnit> generalDay(String dateStr){
        List<RainUnit> rainUnits = new ArrayList<>();

        String formatNow = DateLocalUtils.getGiveFormatNow("yyyy-MM-dd");
        Date dayStart = DateLocalUtils.parseGiveStrToDate(dateStr);
        Date dayEnd = DateLocalUtils.parseStrToEnd(dateStr);


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

        rainUnits.add(rainUnitOne);
        rainUnits.add(rainUnitTwo);
        rainUnits.add(rainUnitThree);
        rainUnits.add(rainUnitFour);
        rainUnits.add(rainUnitFive);


        try {
            if (dateStr == null || dateStr.length() < 9){
                return rainUnits;
            }



            //如果日期不为今天
            if (!dateStr.equals(formatNow)){
                QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
                queryWrapper.between("input_time",dayStart,dayEnd);
                List<RainFallData> rainFallDatas = rainFallDataMapper.selectList(queryWrapper);
                if (rainFallDatas == null || rainFallDatas.size() == 0){
                    return rainUnits;
                }

                return rainFallDataService.calRain(rainFallDatas,dayStart,dayEnd);
            }


            //如果日期为今天
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime oneAgoHour = nowHour.minusHours(1);



            //如果时间在00:00:00 - 02:00:00之间，只用OnenetDev表中数据
            if (nowHour.minusHours(2).isBefore(LocalDate.now().atStartOfDay())){
                //如果时间在00:00:00 - 01:00:00之间
                if (DateLocalUtils.parseTimeToDate(nowHour).equals(dayStart)){
                    return this.selectRealTimeRain();
                }

                //如果时间在01:00:00 - 02:00:00之间
                Set<RainUnit> twoRainFallOne = onenetDev1Service.getTwoRainFall();
                Set<RainUnit> twoRainFallTwo = onenetDev2Service.getTwoRainFall();
                Set<RainUnit> twoRainFallThree = onenetDev3Service.getTwoRainFall();
                Set<RainUnit> twoRainFallFour = onenetDev4Service.getTwoRainFall();
                Set<RainUnit> twoRainFallFive = onenetDev5Service.getTwoRainFall();

                return this.selectRain(twoRainFallOne,twoRainFallTwo,twoRainFallThree,twoRainFallFour,twoRainFallFive,dayStart);

            }


            //时间在02:00:00之后
            Set<RainUnit> twoRainFallOne = onenetDev1Service.getTwoRainFall();
            Set<RainUnit> twoRainFallTwo = onenetDev2Service.getTwoRainFall();
            Set<RainUnit> twoRainFallThree = onenetDev3Service.getTwoRainFall();
            Set<RainUnit> twoRainFallFour = onenetDev4Service.getTwoRainFall();
            Set<RainUnit> twoRainFallFive = onenetDev5Service.getTwoRainFall();



            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",dayStart,DateLocalUtils.parseTimeToDate(oneAgoHour));
            List<RainFallData> rainFallDatas = rainFallDataMapper.selectList(queryWrapper);


            Set<RainUnit> agoRainFallOne = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device1", rainFallData.getInputTime(), rainFallData.getRainfall1()))
                    .collect(Collectors.toSet());

            Set<RainUnit> agoRainFallTwo = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device2", rainFallData.getInputTime(), rainFallData.getRainfall2()))
                    .collect(Collectors.toSet());


            Set<RainUnit> agoRainFallThree = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device3", rainFallData.getInputTime(), rainFallData.getRainfall3()))
                    .collect(Collectors.toSet());


            Set<RainUnit> agoRainFallFour = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device4", rainFallData.getInputTime(), rainFallData.getRainfall4()))
                    .collect(Collectors.toSet());


            Set<RainUnit> agoRainFallFive = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device5", rainFallData.getInputTime(), rainFallData.getRainfall5()))
                    .collect(Collectors.toSet());

            agoRainFallOne.addAll(twoRainFallOne);
            agoRainFallTwo.addAll(twoRainFallTwo);
            agoRainFallThree.addAll(twoRainFallThree);
            agoRainFallFour.addAll(twoRainFallFour);
            agoRainFallFive.addAll(twoRainFallFive);


            return this.selectRain(agoRainFallOne,agoRainFallTwo,agoRainFallThree,agoRainFallFour,agoRainFallFive,dayStart);

        }catch (Exception e){
            logger.error("获取历史雨量数据发生异常",e);
        }
        return rainUnits;
    }


    public List<RainUnit> selectRain(Set<RainUnit> twoRainFallOne,Set<RainUnit> twoRainFallTwo,
                                        Set<RainUnit> twoRainFallThree,Set<RainUnit> twoRainFallFour,
                                        Set<RainUnit> twoRainFallFive,Date dayStart){
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

            List<RainUnit> rainOne = twoRainFallOne.stream()
                    .filter(rainUnit -> rainUnit != null && rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainOne.size() == 0){
                rainUnitOne.setValue(null);
            }else {
                double sum = rainOne.stream()
                        .mapToDouble(RainUnit::getValue)
                        .sum();
                rainUnitOne.setValue((float)sum);
            }



            List<RainUnit> rainTwo = twoRainFallTwo.stream()
                    .filter(rainUnit -> rainUnit != null && rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainTwo.size() == 0){
                rainUnitTwo.setValue(null);
            }else {
                double sum = rainTwo.stream()
                        .mapToDouble(RainUnit::getValue)
                        .sum();
                rainUnitTwo.setValue((float)sum);
            }



            List<RainUnit> rainThree = twoRainFallThree.stream()
                    .filter(rainUnit -> rainUnit != null && rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainThree.size() == 0){
                rainUnitThree.setValue(null);
            }else {
                double sum = rainThree.stream()
                        .mapToDouble(RainUnit::getValue)
                        .sum();
                rainUnitThree.setValue((float)sum);
            }


            List<RainUnit> rainFour = twoRainFallFour.stream()
                    .filter(rainUnit -> rainUnit != null && rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainFour.size() == 0){
                rainUnitFour.setValue(null);
            }else {
                double sum = rainFour.stream()
                        .mapToDouble(RainUnit::getValue)
                        .sum();
                rainUnitFour.setValue((float)sum);
            }


            List<RainUnit> rainFive = twoRainFallFive.stream()
                    .filter(rainUnit -> rainUnit != null && rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainFive.size() == 0){
                rainUnitFive.setValue(null);
            }else {
                double sum = rainFive.stream()
                        .mapToDouble(RainUnit::getValue)
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
            logger.error("获取5个设备雨量数据出错",e);
        }
        return rainUnits;
    }





    public OnenetResponse dataByGivenHour(Integer agoHourNum){
        OnenetResponse response = new OnenetResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            List<RainHisUnit> units = rainFallDataService.dataByGivenHour(agoHourNum);
            if (units != null && units.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取onenet历史数据成功");
                response.setRainHisUnits(units);
                return response;
            }
        }catch (Exception e){
            logger.error("获取onenet历史数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取onenet历史数据失败");
        return response;
    }





}
