package com.stg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.GNSSData;
import com.stg.mapper.GNSSDataMapper;
import com.stg.service.GNSSDataService;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.southVo.DataLatestUnit;
import com.stg.vo.southVo.GNSSReportUnit;
import com.stg.vo.southVo.GNSSUnit;
import com.stg.vo.southVo.MonitorUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GNSSDataServiceImpl extends ServiceImpl<GNSSDataMapper, GNSSData> implements GNSSDataService {

    @Resource
    GNSSDataMapper gnssDataMapper;

    @Resource
    SafetyWarnServiceImpl safetyWarnService;


    private static final Logger logger = LoggerFactory.getLogger(GNSSDataServiceImpl.class);


    @Override
    public Boolean LocalSaveOrUpdata(DataLatestUnit Latest, String platformId, Integer moduleId, String deviceName) {
        try{
            String JC01Time = Latest.getTime();
            Double JC01X = Latest.getTargetVariationPlaneX();
            Double JC01Y = Latest.getTargetVariationPlaneY();
            Double JC01H = Latest.getTargetVariationPlaneH();

            GNSSData gnssData = new GNSSData();
            gnssData.setPlatformId(platformId);
            gnssData.setModuleId(moduleId);
            gnssData.setDeviceName(deviceName);
            gnssData.setTime(DateLocalUtils.parseStrToDate(JC01Time));
            gnssData.setTargetVariationPlaneX(JC01X);
            gnssData.setTargetVariationPlaneY(JC01Y);
            gnssData.setTargetVariationPlaneH(JC01H);


            if (gnssData == null || gnssData.getTime() == null){
                logger.info("解析接收到的数据为空，不执行插入操作{}",deviceName);
                return true;
            }

            //logger.info("接收到的gnssData为{}", JSON.toJSONString(gnssData));

            QueryWrapper<GNSSData> wrapper = new QueryWrapper<>();
            wrapper.eq("time",gnssData.getTime());
            wrapper.eq("device_name",gnssData.getDeviceName());
            GNSSData selectOne = gnssDataMapper.selectOne(wrapper);
            if (selectOne != null && selectOne.getId() != null){
                logger.info("数据库表中已有记录，不执行插入操作{}",deviceName);
                return true;
            }

            safetyWarnService.saveGNSS(gnssData);

            return save(gnssData);
        }catch (Exception e){
            logger.error("{}在插入或更新时报错",deviceName,e);
        }
        return false;
    }



    @Override
    public List<MonitorUnit> selectLatestGNSS() {
        try {

            List<GNSSData> gnssDatas = gnssDataMapper.selectLatestGNSS();
            if (gnssDatas == null || gnssDatas.size() == 0){
                return null;
            }


            List<MonitorUnit> monitorUnits = new ArrayList<>();

            monitorUnits.add(new MonitorUnit("牛岭水库M1",null,gnssDatas.stream()
                    .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                    .map(gnssData -> new GNSSUnit(DateLocalUtils.parseDateToStr(gnssData.getTime()), gnssData.getTargetVariationPlaneX(),
                            gnssData.getTargetVariationPlaneY(), gnssData.getTargetVariationPlaneH()))
                    .collect(Collectors.toList())));


            monitorUnits.add(new MonitorUnit("牛岭水库M2",null,gnssDatas.stream()
                    .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                    .map(gnssData -> new GNSSUnit(DateLocalUtils.parseDateToStr(gnssData.getTime()), gnssData.getTargetVariationPlaneX(),
                            gnssData.getTargetVariationPlaneY(), gnssData.getTargetVariationPlaneH()))
                    .collect(Collectors.toList())));


            monitorUnits.add(new MonitorUnit("牛岭水库M3",null,gnssDatas.stream()
                    .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                    .map(gnssData -> new GNSSUnit(DateLocalUtils.parseDateToStr(gnssData.getTime()), gnssData.getTargetVariationPlaneX(),
                            gnssData.getTargetVariationPlaneY(), gnssData.getTargetVariationPlaneH()))
                    .collect(Collectors.toList())));





            return   monitorUnits;


        }catch (Exception e){
            logger.error("在获取GNSS数据时发生异常",e);
        }
        return null;
    }


    @Override
    public List<MonitorUnit> selectFortyEight() {
        try {

            QueryWrapper<GNSSData> queryWrapper = new QueryWrapper<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime agoHour = nowHour.minusHours(48);
            queryWrapper.between("time",agoHour,nowHour);
            queryWrapper.orderByAsc("time");
            List<GNSSData> gnssDatas = gnssDataMapper.selectList(queryWrapper);
            if (gnssDatas == null || gnssDatas.size() == 0){
                return null;
            }

            List<MonitorUnit> monitorUnits = new ArrayList<>();
            List<GNSSUnit> oneUnit = new ArrayList<>();
            List<GNSSUnit> twoUnit = new ArrayList<>();
            List<GNSSUnit> threeUnit = new ArrayList<>();
            for (int i = 0; i < 49; i++) {
                GNSSUnit one = new GNSSUnit();
                GNSSUnit two = new GNSSUnit();
                GNSSUnit three = new GNSSUnit();
                LocalDateTime startHour = agoHour.plusHours(i);
                List<GNSSData> dateOne = gnssDatas.stream()
                        .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                        .filter(gnssData -> startHour.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .collect(Collectors.toList());

                if (dateOne == null || dateOne.size() == 0){
                    one.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    one.setTargetVariationPlaneX(null);
                    one.setTargetVariationPlaneY(null);
                    one.setTargetVariationPlaneH(null);
                }else {
                    one.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    one.setTargetVariationPlaneX(dateOne.get(0).getTargetVariationPlaneX());
                    one.setTargetVariationPlaneY(dateOne.get(0).getTargetVariationPlaneY());
                    one.setTargetVariationPlaneH(dateOne.get(0).getTargetVariationPlaneH());
                }
                oneUnit.add(one);



                List<GNSSData> dateTwo = gnssDatas.stream()
                        .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                        .filter(gnssData -> startHour.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .collect(Collectors.toList());

                if (dateTwo == null || dateTwo.size() == 0){
                    two.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    two.setTargetVariationPlaneX(null);
                    two.setTargetVariationPlaneY(null);
                    two.setTargetVariationPlaneH(null);
                }else {
                    two.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    two.setTargetVariationPlaneX(dateTwo.get(0).getTargetVariationPlaneX());
                    two.setTargetVariationPlaneY(dateTwo.get(0).getTargetVariationPlaneY());
                    two.setTargetVariationPlaneH(dateTwo.get(0).getTargetVariationPlaneH());
                }
                twoUnit.add(two);


                List<GNSSData> dateThree = gnssDatas.stream()
                        .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                        .filter(gnssData -> startHour.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .collect(Collectors.toList());

                if (dateThree == null || dateThree.size() == 0){
                    three.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    three.setTargetVariationPlaneX(null);
                    three.setTargetVariationPlaneY(null);
                    three.setTargetVariationPlaneH(null);
                }else {
                    three.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    three.setTargetVariationPlaneX(dateThree.get(0).getTargetVariationPlaneX());
                    three.setTargetVariationPlaneY(dateThree.get(0).getTargetVariationPlaneY());
                    three.setTargetVariationPlaneH(dateThree.get(0).getTargetVariationPlaneH());
                }
                threeUnit.add(three);
            }


            monitorUnits.add(new MonitorUnit("牛岭水库M1",null,oneUnit));
            monitorUnits.add(new MonitorUnit("牛岭水库M2",null,twoUnit));
            monitorUnits.add(new MonitorUnit("牛岭水库M3",null,threeUnit));





/*            monitorUnits.add(new MonitorUnit("牛岭水库M1",gnssDatas.stream()
                    .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                    .map(gnssData -> new GNSSUnit(DateLocalUtils.parseDateToStr(gnssData.getTime()), gnssData.getTargetVariationPlaneX(),
                            gnssData.getTargetVariationPlaneY(), gnssData.getTargetVariationPlaneH()))
                    .collect(Collectors.toList())));


            monitorUnits.add(new MonitorUnit("牛岭水库M2",gnssDatas.stream()
                    .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                    .map(gnssData -> new GNSSUnit(DateLocalUtils.parseDateToStr(gnssData.getTime()), gnssData.getTargetVariationPlaneX(),
                            gnssData.getTargetVariationPlaneY(), gnssData.getTargetVariationPlaneH()))
                    .collect(Collectors.toList())));


            monitorUnits.add(new MonitorUnit("牛岭水库M3",gnssDatas.stream()
                    .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                    .map(gnssData -> new GNSSUnit(DateLocalUtils.parseDateToStr(gnssData.getTime()), gnssData.getTargetVariationPlaneX(),
                            gnssData.getTargetVariationPlaneY(), gnssData.getTargetVariationPlaneH()))
                    .collect(Collectors.toList())));*/





            return   monitorUnits;


        }catch (Exception e){
            logger.error("获取GNSS48小时数据发生异常",e);
        }


        return null;
    }

    @Override
    public List<MonitorUnit> dataByGivenHourGNSS(Integer agoHourNum) {
        try {
            if (agoHourNum == null || agoHourNum == 0){
                return null;
            }

            QueryWrapper<GNSSData> queryWrapper = new QueryWrapper<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime agoHour = nowHour.minusHours(agoHourNum);
            queryWrapper.between("time",agoHour,nowHour);
            queryWrapper.orderByAsc("time");
            List<GNSSData> gnssDatas = gnssDataMapper.selectList(queryWrapper);
            if (gnssDatas == null || gnssDatas.size() == 0){
                return null;
            }

            List<MonitorUnit> monitorUnits = new ArrayList<>();
            List<GNSSUnit> oneUnit = new ArrayList<>();
            List<GNSSUnit> twoUnit = new ArrayList<>();
            List<GNSSUnit> threeUnit = new ArrayList<>();
            for (int i = 0; i < agoHourNum+1; i++) {
                GNSSUnit one = new GNSSUnit();
                GNSSUnit two = new GNSSUnit();
                GNSSUnit three = new GNSSUnit();
                LocalDateTime startHour = agoHour.plusHours(i);
                List<GNSSData> dateOne = gnssDatas.stream()
                        .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                        .filter(gnssData -> startHour.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .collect(Collectors.toList());

                if (dateOne.size() == 0){
                    one.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    one.setTargetVariationPlaneX(null);
                    one.setTargetVariationPlaneY(null);
                    one.setTargetVariationPlaneH(null);
                }else {
                    one.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    one.setTargetVariationPlaneX(dateOne.get(0).getTargetVariationPlaneX());
                    one.setTargetVariationPlaneY(dateOne.get(0).getTargetVariationPlaneY());
                    one.setTargetVariationPlaneH(dateOne.get(0).getTargetVariationPlaneH());
                }
                oneUnit.add(one);



                List<GNSSData> dateTwo = gnssDatas.stream()
                        .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                        .filter(gnssData -> startHour.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .collect(Collectors.toList());

                if (dateTwo.size() == 0){
                    two.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    two.setTargetVariationPlaneX(null);
                    two.setTargetVariationPlaneY(null);
                    two.setTargetVariationPlaneH(null);
                }else {
                    two.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    two.setTargetVariationPlaneX(dateTwo.get(0).getTargetVariationPlaneX());
                    two.setTargetVariationPlaneY(dateTwo.get(0).getTargetVariationPlaneY());
                    two.setTargetVariationPlaneH(dateTwo.get(0).getTargetVariationPlaneH());
                }
                twoUnit.add(two);


                List<GNSSData> dateThree = gnssDatas.stream()
                        .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                        .filter(gnssData -> startHour.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .collect(Collectors.toList());

                if (dateThree.size() == 0){
                    three.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    three.setTargetVariationPlaneX(null);
                    three.setTargetVariationPlaneY(null);
                    three.setTargetVariationPlaneH(null);
                }else {
                    three.setTime(DateLocalUtils.getGiveTimeToStr(startHour,"yyyy-MM-dd HH:mm:ss"));
                    three.setTargetVariationPlaneX(dateThree.get(0).getTargetVariationPlaneX());
                    three.setTargetVariationPlaneY(dateThree.get(0).getTargetVariationPlaneY());
                    three.setTargetVariationPlaneH(dateThree.get(0).getTargetVariationPlaneH());
                }
                threeUnit.add(three);
            }


            List<GNSSUnit> oneHave = oneUnit.stream().filter(gnssUnit -> gnssUnit.getTargetVariationPlaneH() != null)
                    .collect(Collectors.toList());
            List<GNSSUnit> twoHave = twoUnit.stream().filter(gnssUnit -> gnssUnit.getTargetVariationPlaneH() != null)
                    .collect(Collectors.toList());
            List<GNSSUnit> threeHave = threeUnit.stream().filter(gnssUnit -> gnssUnit.getTargetVariationPlaneH() != null)
                    .collect(Collectors.toList());


            monitorUnits.add(new MonitorUnit("牛岭水库M1",oneHave.size() == 0?null:oneHave.get(oneHave.size()-1),oneUnit));
            monitorUnits.add(new MonitorUnit("牛岭水库M2",twoHave.size() == 0?null:twoHave.get(twoHave.size()-1),twoUnit));
            monitorUnits.add(new MonitorUnit("牛岭水库M3",threeHave.size() == 0?null:threeHave.get(threeHave.size()-1),threeUnit));
            return   monitorUnits;


        }catch (Exception e){
            logger.error("获取GNSS历史数据发生异常",e);
        }
        return null;
    }



    @Override
    public List<GNSSReportUnit> selectGNSSReport(String dateStr) {
        try {
            if (dateStr == null || dateStr.length() < 10){
                return null;
            }

            Date end = DateLocalUtils.parseStrToEnd(dateStr);
            Date start = DateLocalUtils.parseStrToStart(dateStr);
            QueryWrapper<GNSSData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,end);
            queryWrapper.orderByAsc("time");
            List<GNSSData> gnssDatas = gnssDataMapper.selectList(queryWrapper);
            if (gnssDatas == null || gnssDatas.size() == 0){
                return null;
            }

            List<GNSSReportUnit> units = new ArrayList<>();
            LocalDateTime startTime = DateLocalUtils.parseDateToTime(start);
            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < 24; i++) {
                GNSSReportUnit unit = new GNSSReportUnit();
                LocalDateTime cycleStart = startTime.plusHours(i);

                if (cycleStart.isAfter(now)){
                    break;
                }

                unit.setInputTime(DateLocalUtils.parseTimeToStr(cycleStart));


                List<GNSSData> oneList = gnssDatas.stream().filter(gnssData -> cycleStart.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .filter(gnssData -> "牛岭水库M1".equals(gnssData.getDeviceName()))
                        .collect(Collectors.toList());

                if (oneList.size() > 0){
                    unit.setOneX(oneList.get(0).getTargetVariationPlaneX());
                    unit.setOneY(oneList.get(0).getTargetVariationPlaneY());
                    unit.setOneH(oneList.get(0).getTargetVariationPlaneH());
                }else {
                    unit.setOneX(null);
                    unit.setOneY(null);
                    unit.setOneH(null);
                }



                List<GNSSData> twoList = gnssDatas.stream().filter(gnssData -> cycleStart.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .filter(gnssData -> "牛岭水库M2".equals(gnssData.getDeviceName()))
                        .collect(Collectors.toList());


                if (twoList.size() > 0){
                    unit.setTwoX(twoList.get(0).getTargetVariationPlaneX());
                    unit.setTwoY(twoList.get(0).getTargetVariationPlaneY());
                    unit.setTwoH(twoList.get(0).getTargetVariationPlaneH());
                }else {
                    unit.setTwoX(null);
                    unit.setTwoY(null);
                    unit.setTwoH(null);
                }

                List<GNSSData> threeList = gnssDatas.stream().filter(gnssData -> cycleStart.equals(DateLocalUtils.parseDateToTime(gnssData.getTime())))
                        .filter(gnssData -> "牛岭水库M3".equals(gnssData.getDeviceName()))
                        .collect(Collectors.toList());

                if (threeList.size() > 0){
                    unit.setThreeX(threeList.get(0).getTargetVariationPlaneX());
                    unit.setThreeY(threeList.get(0).getTargetVariationPlaneY());
                    unit.setThreeH(threeList.get(0).getTargetVariationPlaneH());
                }else {
                    unit.setThreeX(null);
                    unit.setThreeY(null);
                    unit.setThreeH(null);
                }

                units.add(unit);

            }

            return units;


        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


}
