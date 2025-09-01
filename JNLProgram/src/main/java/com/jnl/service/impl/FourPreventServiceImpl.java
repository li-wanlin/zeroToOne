package com.jnl.service.impl;

import com.alibaba.fastjson.JSON;
import com.jnl.entity.*;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.fourPreventVo.RainData;
import com.jnl.vo.fourPreventVo.RainRangeUnit;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.matlabVo.MatlabPlanResponse;
import com.jnl.vo.matlabVo.MatlabResponse;
import com.jnl.vo.matlabVo.ResUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FourPreventServiceImpl {


    @Resource
    WeatherServiceImpl weatherService;

    @Resource
    RainFallDataServiceImpl rainFallDataService;


    @Resource
    ForecastFlowServiceImpl forecastFlowService;


    @Resource
    FloodForecastServiceImpl floodForecastService;

    @Resource
    FloodControlServiceImpl floodControlService;

    @Resource
    FloodEvoluteServiceImpl floodEvoluteService;


    @Resource
    HistoryFlowServiceImpl historyFlowService;


    private static final Logger logger = LoggerFactory.getLogger(FourPreventServiceImpl.class);


    /**
     * 获取近四天降雨量详情
     * @return
     */
    public List<RainRangeUnit> selectRainList(){
        try {
            List<RainRangeUnit> unitsSuf = weatherService.forecastThree();
            List<RainRangeUnit> unitsPre = rainFallDataService.dayAgo();

            unitsPre.addAll(unitsSuf);
            return unitsPre;
        }catch (Exception e){
            logger.error("获取最近4天雨量数据发生异常",e);
        }
        return null;
    }


    /**
     * 模拟获取近四天降雨量详情
     * @return
     */
    public List<RainRangeUnit> selectRainListSim(){
        try {
            double[] array = {0,2,10,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0.64 ,1.27 ,1.27 ,3.18 ,5.08 ,6.35 ,9.53 ,9.53 ,12.70 ,29.21 ,25.40 ,22.86 ,20.96 ,15.88 ,13.97 ,11.43 ,8.26 ,6.99 ,5.08 ,3.18 ,1.27 ,1.27 ,0.64 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime startHour = nowHour.minusHours(24);

            List<RainRangeUnit> units = new ArrayList<>();

            for (int i = 0; i < array.length; i++) {
                RainRangeUnit unit = new RainRangeUnit();
                unit.setValue(array[i]);
                unit.setRainTime(DateLocalUtils.parseTimeToStr(startHour.plusHours(i)));
                units.add(unit);
            }

            return units;
        }catch (Exception e){
            logger.error("模拟获取最近4天雨量数据发生异常",e);
        }
        return null;
    }


    /**
     * 模拟获取近四天降雨结果
     * @return
     */
    public RainData calRainSim(){
        double[] array = {0,2,10,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0.64 ,1.27 ,1.27 ,3.18 ,5.08 ,6.35 ,9.53 ,9.53 ,12.70 ,29.21 ,25.40 ,22.86 ,20.96 ,15.88 ,13.97 ,11.43 ,8.26 ,6.99 ,5.08 ,3.18 ,1.27 ,1.27 ,0.64 ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        RainData rainData = new RainData();
        rainData.setDayAgo(Arrays.stream(array, 0, 24).sum());
        rainData.setDayAfter(Arrays.stream(array, 25, 48).sum());
        rainData.setDayTwoAfter(Arrays.stream(array, 25, 72).sum());
        rainData.setDayThreeAfter(Arrays.stream(array).sum());
        return rainData;
    }




    /**
     * 获取近四天降雨结果
     * @return
     */
    public RainData calRain(){
        try {
            RainData rainData = new RainData();
            List<RainRangeUnit> unitsSuf = weatherService.forecastThree();
            List<RainRangeUnit> unitsPre = rainFallDataService.dayAgo();

            unitsPre.addAll(unitsSuf);


            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime dayAgo = nowHour.minusHours(24);
            LocalDateTime dayAfter = nowHour.plusHours(23);
            LocalDateTime dayTwoAfter = nowHour.plusHours(47);
            LocalDateTime dayThreeAfter = nowHour.plusHours(71);

            List<RainRangeUnit> agoList = unitsPre.stream().filter(rainRangeUnit -> dayAgo.isBefore(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || dayAgo.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .filter(rainRangeUnit -> nowHour.isAfter(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .collect(Collectors.toList());




            List<RainRangeUnit> afterList = unitsPre.stream().filter(rainRangeUnit -> nowHour.isBefore(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || nowHour.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .filter(rainRangeUnit -> dayAfter.isAfter(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || dayAfter.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .collect(Collectors.toList());




            List<RainRangeUnit> twoAfterList = unitsPre.stream().filter(rainRangeUnit -> nowHour.isBefore(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || nowHour.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .filter(rainRangeUnit -> dayTwoAfter.isAfter(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || dayTwoAfter.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .collect(Collectors.toList());




            List<RainRangeUnit> threeAfterList = unitsPre.stream().filter(rainRangeUnit -> nowHour.isBefore(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || nowHour.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .filter(rainRangeUnit -> dayThreeAfter.isAfter(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime()))
                            || dayThreeAfter.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())))
                    .collect(Collectors.toList());




            rainData.setDayAgo(agoList.stream().filter(rainRangeUnit -> rainRangeUnit.getValue()!=null).mapToDouble(RainRangeUnit::getValue).sum());
            rainData.setDayAfter(afterList.stream().mapToDouble(RainRangeUnit::getValue).sum());
            rainData.setDayTwoAfter(twoAfterList.stream().mapToDouble(RainRangeUnit::getValue).sum());
            rainData.setDayThreeAfter(threeAfterList.stream().mapToDouble(RainRangeUnit::getValue).sum());

            return rainData;

        }catch (Exception e){
            logger.error("获取最近4天雨量计算数据发生异常",e);
        }
        return null;
    }


    /**
     * 获取入库流量预报数据
     * @return
     */
    public MatlabResponse selectForecastFlow(){
        MatlabResponse response = new MatlabResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            ForecastFlow flow = forecastFlowService.selectForecastFlow();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneAgo = now.minusDays(1);
            LocalDateTime twoAgo = now.minusDays(2);
            LocalDateTime threeAgo = now.minusDays(3);
            LocalDateTime fourAgo = now.minusDays(4);
            LocalDateTime fiveAgo = now.minusDays(5);


            ResUnit unitNow = new ResUnit();
            ResUnit unitOne = new ResUnit();
            ResUnit unitTwo = new ResUnit();
            ResUnit unitThree = new ResUnit();
            ResUnit unitFour = new ResUnit();
            ResUnit unitFive = new ResUnit();


            List<ResUnit> units = new ArrayList<>();

            if (flow == null){
                flow = historyFlowService.fiveDayData();
            }

            unitNow.setTime(DateLocalUtils.parseTimeToStrTwo(now));
            unitNow.setValue(flow.getForecast());

            unitOne.setTime(DateLocalUtils.parseTimeToStrTwo(oneAgo));
            unitOne.setValue(flow.getOneDayFlow());

            unitTwo.setTime(DateLocalUtils.parseTimeToStrTwo(twoAgo));
            unitTwo.setValue(flow.getTwoDayFlow());

            unitThree.setTime(DateLocalUtils.parseTimeToStrTwo(threeAgo));
            unitThree.setValue(flow.getThreeDayFlow());

            unitFour.setTime(DateLocalUtils.parseTimeToStrTwo(fourAgo));
            unitFour.setValue(flow.getFourDayFlow());

            unitFive.setTime(DateLocalUtils.parseTimeToStrTwo(fiveAgo));
            unitFive.setValue(flow.getFiveDayFlow());

            units.add(unitFive);
            units.add(unitFour);
            units.add(unitThree);
            units.add(unitTwo);
            units.add(unitOne);
            units.add(unitNow);

            response.setUpdateTime(DateLocalUtils.parseTimeToStr(now.withMinute((now.getMinute()/5)*5).withSecond(0).withNano(0)));
            response.setRycs(units);
            meta.setStatus(200);
            meta.setMsg("获取入库流量预报数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取入库流量预报数据发生异常",e);
        }

        meta.setStatus(400);
        meta.setMsg("获取入库流量预报数据失败");
        return response;
    }


    /**
     * 获取洪水预报、洪水调度、洪水演进数据
     * @return
     */
    public MatlabResponse selectFloodInfo(){
        MatlabResponse response = new MatlabResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        LocalDateTime now = LocalDateTime.now();

        response.setUpdateTime(DateLocalUtils.parseTimeToStr(now.withMinute((now.getMinute()/5)*5).withSecond(0).withNano(0)));
        try {

            //获取洪水预报数据
            FloodForecast floodForecast = floodForecastService.selectFloodForecast();

            if (floodForecast == null){
                return null;
            }

            //代表没有洪水
            if (floodForecast.getFlag() == -1d){
                response.setFlag(floodForecast.getFlag());
                meta.setStatus(200);
                meta.setMsg("获取洪水信息数据成功");
                return response;
            }


            List<ResUnit> xsjfs = null;
            List<ResUnit> skfhStage = null;
            List<ResUnit> skfhFlow = null;
            List<ResUnit> hsyjLwz = null;
            List<ResUnit> hsyjDqg = null;
            List<ResUnit> hsyjByl = null;


            xsjfs = this.doubleArrayToUnit(JSON.parseObject(floodForecast.getFlow(), double[].class));
            //获取洪水调度数据
            FloodControl floodControl = floodControlService.selectFloodControl();
            if (floodControl != null){
                skfhStage = this.doubleArrayToUnit(JSON.parseObject(floodControl.getStage(), double[].class));
                skfhFlow = this.doubleArrayToUnit(JSON.parseObject(floodControl.getFlow(), double[].class));
            }

            //获取洪水演进数据
            FloodEvolute floodEvolute = floodEvoluteService.selectFloodEvolute();
            if (floodEvolute != null){
                hsyjLwz = this.doubleArrayToUnit(JSON.parseObject(floodEvolute.getLwzFlow(), double[].class));
                hsyjDqg = this.doubleArrayToUnit(JSON.parseObject(floodEvolute.getDqgFlow(), double[].class));
                hsyjByl = this.doubleArrayToUnit(JSON.parseObject(floodEvolute.getBylFlow(), double[].class));
            }



            response.setFlag(floodForecast.getFlag());
            response.setXajfs(xsjfs);
            response.setSkfhStage(skfhStage);
            response.setSkfhFlow(skfhFlow);
            response.setHsyjLwz(hsyjLwz);
            response.setHsyjDqg(hsyjDqg);
            response.setHsyjByl(hsyjByl);
            meta.setStatus(200);
            meta.setMsg("获取洪水信息数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取洪水信息数据发生异常",e);
        }
        return null;
    }







    /**
     * 获取洪水预报、洪水调度、洪水演进数据
     * @return
     */
    public MatlabResponse selectFloodInfoSim(){
        MatlabResponse response = new MatlabResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            //获取洪水预报数据

            double[] forecast = {0.9,0.8,0.8,0.8,0.7,0.7,0.7,0.7,0.7,0.6,0.6,0.6,0.6,0.6,0.6,2.3,6.9,11.9,24.8,49.5,83.9,149.4,227,474.3,1418.8,1835.8,1951.1,1951.8,1756.7,1592.6,1418.4,1216.4,1050.7,891.1,734.7,581,459.6,358.5,269.2,201.9,153.6,119.5,95.5,78.7,66.8,58.4,52.3,47.9,44.6,42.1,40.1,38.6,37.3,36.1,35.2,34.3,33.5,32.7,32,31.3,30.7,30.1,29.4,28.8,28.3,27.7,27.1,26.6,26.1,25.5,25,24.5};



            List<ResUnit> xsjfs = null;
            List<ResUnit> skfhStage = null;
            List<ResUnit> skfhFlow = null;
            List<ResUnit> hsyjLwz = null;
            List<ResUnit> hsyjDqg = null;
            List<ResUnit> hsyjByl = null;



            xsjfs = this.doubleArrayToUnit(forecast);
            //获取洪水调度数据

            double[] floodControlFlow = {20,20,20,20,20,20,20,20,20,20,20,20,20,20,0,0,20,0,0,20,20,20,20,20,420.7,1433.6,1868.3,1946.9,1860,1682,1516.4,1325.9,1154.2,996.8,835.9,682.5,561,447.7,347.8,264.1,216.1,177.7,143.7,115.7,95,78.8,66.9,58.1,52.2,47.7,44.8,41.8,40.4,38.9,37.4,35.9,34.5,34.5,33,33,31.5,31.5,30,30,30,28.5,28.5,27.1,27.1,27.1,25.6,22.6};

            double[] floodControlStage = {675.00 ,674.93 ,674.86 ,674.79 ,674.71 ,674.64 ,674.57 ,674.50 ,674.43 ,674.35 ,674.28 ,674.21 ,674.14 ,674.06 ,673.99 ,673.99 ,674.00 ,673.95 ,674.00 ,674.09 ,674.20 ,674.44 ,674.92 ,675.49 ,677.39 ,679.22 ,679.85 ,679.97 ,679.84 ,679.58 ,679.34 ,679.07 ,678.79 ,678.52 ,678.24 ,677.97 ,677.70 ,677.45 ,677.23 ,677.04 ,676.88 ,676.72 ,676.58 ,676.47 ,676.39 ,676.32 ,676.27 ,676.24 ,676.21 ,676.19 ,676.18 ,676.17 ,676.16 ,676.16 ,676.15 ,676.15 ,676.14 ,676.14 ,676.13 ,676.13 ,676.13 ,676.13 ,676.12 ,676.12 ,676.12 ,676.12 ,676.12 ,676.11 ,676.11 ,676.11 ,676.10 ,676.09};


            skfhStage = this.doubleArrayToUnit(floodControlStage);
            skfhFlow = this.doubleArrayToUnit(floodControlFlow);


            //获取洪水演进数据
            double[] floodEvoluteLwz = {0.00 ,30.50 ,14.50 ,22.90 ,18.50 ,20.80 ,19.60 ,20.20 ,19.90 ,20.10 ,20.00 ,20.00 ,20.00 ,20.00 ,9.20 ,0.00 ,10.80 ,14.00 ,0.00 ,10.80 ,24.80 ,17.50 ,21.30 ,19.30 ,236.60 ,1063.70 ,1861.80 ,1914.10 ,1917.20 ,1734.00 ,1565.40 ,1387.90 ,1200.80 ,1044.90 ,884.80 ,727.50 ,593.40 ,482.90 ,375.30 ,288.20 ,225.60 ,190.40 ,152.70 ,123.90 ,100.20 ,83.50 ,69.90 ,60.60 ,53.60 ,49.10 ,45.50 ,42.80 ,40.50 ,39.50 ,37.80 ,36.40 ,34.90 ,34.20 ,33.80 ,32.60 ,32.40 ,31.00 ,30.90 ,29.50 ,30.30 ,29.10 ,28.30 ,27.90 ,26.60 ,27.30 ,26.20 ,23.70};

            double[] floodEvoluteDqg = {0.00 ,9.20 ,14.20 ,16.90 ,18.30 ,19.10 ,19.50 ,19.70 ,19.90 ,19.90 ,20.00 ,20.00 ,20.00 ,20.00 ,23.70 ,12.80 ,3.20 ,14.60 ,7.90 ,0.60 ,9.50 ,14.40 ,17.00 ,18.40 ,0.00 ,7.70 ,585.10 ,1162.10 ,1539.90 ,1720.20 ,1733.00 ,1668.20 ,1542.00 ,1392.20 ,1239.50 ,1081.70 ,920.00 ,775.40 ,642.70 ,522.10 ,412.00 ,328.70 ,265.40 ,214.40 ,172.70 ,139.90 ,113.90 ,93.90 ,78.50 ,67.20 ,58.70 ,52.90 ,48.00 ,44.80 ,42.30 ,40.30 ,38.60 ,36.70 ,35.90 ,34.60 ,34.10 ,32.90 ,32.50 ,31.40 ,30.80 ,30.70 ,29.70 ,29.40 ,28.30 ,27.80 ,27.70 ,27.30};

            double[] floodEvoluteByl = {0.00 ,4.90 ,8.60 ,11.40 ,13.50 ,15.10 ,16.30 ,17.20 ,17.90 ,18.40 ,18.80 ,19.10 ,19.30 ,19.50 ,24.70 ,18.70 ,9.00 ,16.80 ,12.70 ,4.50 ,8.30 ,11.10 ,13.30 ,14.90 ,0.00 ,0.00 ,239.60 ,617.30 ,964.00 ,1228.00 ,1381.00 ,1462.40 ,1472.70 ,1435.00 ,1368.90 ,1277.70 ,1163.30 ,1045.00 ,924.60 ,805.00 ,685.10 ,580.30 ,490.70 ,413.10 ,345.70 ,288.60 ,240.40 ,200.30 ,167.10 ,140.10 ,118.30 ,101.10 ,87.00 ,76.00 ,67.30 ,60.40 ,54.80 ,49.80 ,46.40 ,43.20 ,41.00 ,38.70 ,37.30 ,35.50 ,34.20 ,33.60 ,32.30 ,31.80 ,30.60 ,29.80 ,29.50 ,29.30};


            hsyjLwz = this.doubleArrayToUnit(floodEvoluteLwz);
            hsyjDqg = this.doubleArrayToUnit(floodEvoluteDqg);
            hsyjByl = this.doubleArrayToUnit(floodEvoluteByl);



            LocalDateTime now = LocalDateTime.now();

            response.setUpdateTime(DateLocalUtils.parseTimeToStr(now.withMinute((now.getMinute()/5)*5).withSecond(0).withNano(0)));
            response.setFlag(1d);
            response.setXajfs(xsjfs);
            response.setSkfhStage(skfhStage);
            response.setSkfhFlow(skfhFlow);
            response.setHsyjLwz(hsyjLwz);
            response.setHsyjDqg(hsyjDqg);
            response.setHsyjByl(hsyjByl);
            meta.setStatus(200);
            meta.setMsg("获取洪水信息数据成功");
            return response;


        }catch (Exception e){
            logger.error("获取洪水信息数据发生异常",e);
        }
        return null;
    }





    public MatlabPlanResponse selectPlan(){
        try {
            MatlabPlanResponse response = new MatlabPlanResponse();
            Meta meta = new Meta();
            response.setMeta(meta);
            Map<String, String> map = floodControlService.selectPlan();
            if (map != null){
                response.setPlanLv(Integer.parseInt(map.get("planLv")));
                response.setDisplay(map.get("display"));
                meta.setStatus(200);
                meta.setMsg("获取预案成功");
                return response;
            }

        }catch (Exception e){
            logger.error("获取预案发生异常",e);
        }
        return null;
    }


    public MatlabPlanResponse selectPlanSim(){
        try {
            MatlabPlanResponse response = new MatlabPlanResponse();
            Meta meta = new Meta();
            response.setMeta(meta);
            Map<String, String> map = floodControlService.selectPlanSim();
            if (map != null){
                response.setPlanLv(Integer.parseInt(map.get("planLv")));
                response.setDisplay(map.get("display"));
                meta.setStatus(200);
                meta.setMsg("获取预案成功");
                return response;
            }

        }catch (Exception e){
            logger.error("获取预案发生异常",e);
        }
        return null;
    }





    public Double calDiff(List<RainRangeUnit> units){
        try {
            List<RainRangeUnit> fallData = units.stream()
                    .filter(rainRangeUnit -> rainRangeUnit.getValue() != null)
                    .collect(Collectors.toList());

            if (fallData.size() == 0){
                return null;
            }


            units.stream()
                    .filter(rainRangeUnit -> rainRangeUnit.getValue() == null)
                    .forEach(rainRangeUnit -> rainRangeUnit.setValue(0.0));


            return  IntStream.range(0, units.size() - 1)
                    .mapToDouble(i -> {
                        RainRangeUnit rainPre = units.get(i);
                        RainRangeUnit rainSuf = units.get(i + 1);
                        return (rainSuf.getValue() - rainPre.getValue()) >= 0
                                ? (rainSuf.getValue() - rainPre.getValue()) : rainSuf.getValue();
                    })
                    .sum();
        }catch (Exception e){
            logger.error("计算雨量数据发生异常",e);
        }
        return null;
    }



    public List<ResUnit> doubleArrayToUnit(double[] nums){
        try {

            if (nums == null || nums.length == 0){
                return null;
            }
            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            List<ResUnit> units = new ArrayList<>();
            for (int i = 0; i < nums.length-1; i++) {
                ResUnit unit = new ResUnit();
                unit.setTime(DateLocalUtils.parseTimeToStr(now.plusHours(i)));
                unit.setValue(nums[i]);
                units.add(unit);
            }

            return units;

        }catch (Exception e){
            logger.error("",e);
        }
        return null;

    }










}
