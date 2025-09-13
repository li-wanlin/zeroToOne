package com.stg.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stg.entity.OnenetDev24;
import com.stg.entity.RainFallData;
import com.stg.entity.RealTimeData;
import com.stg.mapper.RainFallDataMapper;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.onenetVo.*;
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
    OnenetDev21ServiceImpl onenetDev21Service;


    @Resource
    OnenetDev22ServiceImpl onenetDev22Service;


    @Resource
    OnenetDev23ServiceImpl onenetDev23Service;


    @Resource
    OnenetDev24ServiceImpl onenetDev24Service;

    @Resource
    OnenetDev1ServiceImpl onenetDev1Service;



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
        rainUnits.add(new RainUnit("device21",realTime.getInputTime(), realTime.getRainfall1()));
        rainUnits.add(new RainUnit("device22",realTime.getInputTime(), realTime.getRainfall2()));
        rainUnits.add(new RainUnit("device1",realTime.getInputTime(), realTime.getRainfall3()));


        return rainUnits;
    }


    /**
     * 获取onenet设备实时数据
     * @return
     */
    public List<DevUnit> selectLatest(){
        ArrayList<DevUnit> devUnits = new ArrayList<>();
        try {


            devUnits.add(onenetDev21Service.selectLatest());
            devUnits.add(onenetDev22Service.selectLatest());

            devUnits.add(onenetDev23Service.selectLatest());
            devUnits.add(onenetDev24Service.selectLatest());


            devUnits.add(onenetDev1Service.selectLatest());

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
        rainUnitOne.setDevice("device21");
        rainUnitOne.setUpdateTime(dayStart);

        RainUnit rainUnitTwo = new RainUnit();
        rainUnitTwo.setDevice("device22");
        rainUnitTwo.setUpdateTime(dayStart);


        RainUnit rainUnitThree = new RainUnit();
        rainUnitThree.setDevice("device1");
        rainUnitThree.setUpdateTime(dayStart);


        rainUnits.add(rainUnitOne);
        rainUnits.add(rainUnitTwo);
        rainUnits.add(rainUnitThree);


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
                Set<RainUnit> twoRainFallOne = onenetDev21Service.getTwoRainFall();
                Set<RainUnit> twoRainFallTwo = onenetDev22Service.getTwoRainFall();
                Set<RainUnit> twoRainFallThree = onenetDev1Service.getTwoRainFall();


                return this.selectRain(twoRainFallOne,twoRainFallTwo,twoRainFallThree,dayStart);

            }


            //时间在02:00:00之后
            Set<RainUnit> twoRainFallOne = onenetDev21Service.getTwoRainFall();
            Set<RainUnit> twoRainFallTwo = onenetDev22Service.getTwoRainFall();
            Set<RainUnit> twoRainFallThree = onenetDev1Service.getTwoRainFall();




            QueryWrapper<RainFallData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("input_time",dayStart,DateLocalUtils.parseTimeToDate(oneAgoHour));
            List<RainFallData> rainFallDatas = rainFallDataMapper.selectList(queryWrapper);


            Set<RainUnit> agoRainFallOne = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device21", rainFallData.getInputTime(), rainFallData.getRainfall1()))
                    .collect(Collectors.toSet());

            Set<RainUnit> agoRainFallTwo = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device22", rainFallData.getInputTime(), rainFallData.getRainfall2()))
                    .collect(Collectors.toSet());


            Set<RainUnit> agoRainFallThree = rainFallDatas.stream()
                    .map(rainFallData -> new RainUnit("device1", rainFallData.getInputTime(), rainFallData.getRainfall3()))
                    .collect(Collectors.toSet());


            agoRainFallOne.addAll(twoRainFallOne);
            agoRainFallTwo.addAll(twoRainFallTwo);
            agoRainFallThree.addAll(twoRainFallThree);



            return this.selectRain(agoRainFallOne,agoRainFallTwo,agoRainFallThree,dayStart);

        }catch (Exception e){
            logger.error("获取历史雨量数据发生异常",e);
        }
        return rainUnits;
    }


    /**
     * 获取雨量数据
     * @param twoRainFallOne
     * @param twoRainFallTwo
     * @param twoRainFallThree
     * @param dayStart
     * @return
     */
    public List<RainUnit> selectRain(Set<RainUnit> twoRainFallOne,Set<RainUnit> twoRainFallTwo,Set<RainUnit> twoRainFallThree,Date dayStart){
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




            rainUnits.add(rainUnitOne);
            rainUnits.add(rainUnitTwo);
            rainUnits.add(rainUnitThree);


            return rainUnits;


        }catch (Exception e){
            logger.error("获取2个设备雨量数据出错",e);
        }
        return rainUnits;
    }


    /**
     * 获取历史数据
     * @param agoHourNum
     * @return
     */
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


    /**
     * 获取库容和水位数据
     * @return
     */
    public CapacityResponse selectCapaInfo(){
        CapacityResponse response = new CapacityResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            return onenetDev22Service.selectCapaInfo();
        }catch (Exception e){
            logger.error("获取库容水位信息数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取库容水位信息数据失败");
        return response;
    }


    /**
     * 获取设备4水质信息
     * @return
     */
    public WaterInfoResponse selectWaterInfo(){
        WaterInfoResponse response = new WaterInfoResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            OnenetDev24 onenetDev24 = onenetDev24Service.selectWaterInfo();
            if (onenetDev24 !=null && onenetDev24.getId() != null){
                meta.setStatus(200);
                meta.setMsg("获取水质信息数据成功");
                response.setUpdateTime(DateLocalUtils.parseDateToStr(onenetDev24.getUpdateTime()));
                response.setLv(onenetDev24.getLv() != null ? Math.round((double)onenetDev24.getLv() * 100)/100.0 : null);
                response.setCOD(onenetDev24.getCOD() != null ? Math.round((double)onenetDev24.getCOD() * 100)/100.0 : null);
                response.setDO(onenetDev24.getDO() != null ? Math.round((double)onenetDev24.getDO() * 100)/100.0 : null);
                response.setEC(onenetDev24.getEC() != null ? Math.round((double)onenetDev24.getEC() * 100)/100.0 : null);
                response.setLvTemp(onenetDev24.getLvTemp() != null ? Math.round((double)onenetDev24.getLvTemp() * 100)/100.0 : null);
                response.setNHN(onenetDev24.getNHN() != null ? Math.round((double)onenetDev24.getNHN() * 100)/100.0 : null);
                response.setPH(onenetDev24.getPH() != null ? Math.round((double)onenetDev24.getPH() * 100)/100.0 : null);
                response.setZD(onenetDev24.getZD() != null ? Math.round((double)onenetDev24.getZD() * 100)/100.0 : null);
                return response;
            }
        }catch (Exception e){
            logger.error("获取水质信息数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取水质信息数据失败");
        return response;
    }


    /**
     * 获取水质详情数据
     * @param waterType
     * @return
     */
    public WaterDetailResponse selectWaterDetail(String waterType){
        WaterDetailResponse response = new WaterDetailResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<WaterUnit> waterUnits = onenetDev24Service.selectWaterDetail(waterType);
            if (waterUnits !=null && waterUnits.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取水质详情数据成功");
                response.setWaterUnits(waterUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取水质详情数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取水质详情数据失败");
        return response;
    }




}
