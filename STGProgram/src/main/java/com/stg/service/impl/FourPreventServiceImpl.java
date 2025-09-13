package com.stg.service.impl;

import com.alibaba.fastjson.JSON;
import com.stg.entity.*;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.fourPreventVo.RainData;
import com.stg.vo.fourPreventVo.RainRangeUnit;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.matlabVo.MatlabPlanResponse;
import com.stg.vo.matlabVo.MatlabResponse;
import com.stg.vo.matlabVo.ResUnit;
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
            double[] array = {0,2,10,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.92,3.85,3.85,9.62,15.39,19.24,28.86,28.86,38.48,88.51,76.97,69.27,63.50,48.10,42.33,34.64,25.01,21.17,15.39,9.62,3.85,3.85,1.92,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

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
        double[] array = {0,2,10,2,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.92,3.85,3.85,9.62,15.39,19.24,28.86,28.86,38.48,88.51,76.97,69.27,63.50,48.10,42.33,34.64,25.01,21.17,15.39,9.62,3.85,3.85,1.92,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

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
                            || nowHour.isEqual(DateLocalUtils.parseStrToTime(rainRangeUnit.getRainTime())) )
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
/*            ForecastFlow flow = forecastFlowService.selectForecastFlow();

            if (flow == null){
                return null;
            }*/

            //生成显示数据，只有日期变，数据不变

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

            /**
             * xAxisData: ['06-22', '06-23', '06-24', '06-25', '06-26', '06-27'],
             *       seriesData: [0.54, 0.62, 0.58, 0.43, 0.42, 1.25]
             */

            List<ResUnit> units = new ArrayList<>();

            unitNow.setTime(DateLocalUtils.parseTimeToStrTwo(now));
            unitNow.setValue(1.25);

            unitOne.setTime(DateLocalUtils.parseTimeToStrTwo(oneAgo));
            unitOne.setValue(0.42);

            unitTwo.setTime(DateLocalUtils.parseTimeToStrTwo(twoAgo));
            unitTwo.setValue(0.43);

            unitThree.setTime(DateLocalUtils.parseTimeToStrTwo(threeAgo));
            unitThree.setValue(0.58);

            unitFour.setTime(DateLocalUtils.parseTimeToStrTwo(fourAgo));
            unitFour.setValue(0.62);

            unitFive.setTime(DateLocalUtils.parseTimeToStrTwo(fiveAgo));
            unitFive.setValue(0.54);

            units.add(unitFive);
            units.add(unitFour);
            units.add(unitThree);
            units.add(unitTwo);
            units.add(unitOne);
            units.add(unitNow);

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
            //List<ResUnit> hsyjByl = null;


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
                //hsyjByl = this.doubleArrayToUnit(JSON.parseObject(floodEvolute.getBylFlow(), double[].class));
            }


            response.setFlag(floodForecast.getFlag());
            response.setXajfs(xsjfs);
            response.setSkfhStage(skfhStage);
            response.setSkfhFlow(skfhFlow);
            response.setHsyjLwz(hsyjLwz);
            response.setHsyjDqg(hsyjDqg);
            //response.setHsyjByl(hsyjByl);
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

            double[] forecast = {0.04,0.03,0.03,0.03,0.03,0.03,0.03,0.03,0.03,0.03,0.03,0.03,0.03,0.02,0.31,1.05,1.84,4.40,10.83,29.77,66.48,89.16,126.12,305.44,327.87,312.03,290.28,235.82,202.54,171.04,136.66,114.54,93.92,74.52,56.54,44.73,35.07,26.07,19.36,14.56,11.18,8.82,7.16,5.99,5.17,4.58,4.15,3.83,3.60,3.41,3.27,3.15,3.05,2.97,2.89,2.82,2.75,2.69,2.64,2.58,2.53,2.48,2.42,2.38,2.33,2.28,2.23,2.19,2.15,2.10,2.06,2.02};



            List<ResUnit> xsjfs = null;
            List<ResUnit> skfhStage = null;
            List<ResUnit> skfhFlow = null;
            List<ResUnit> hsyjLwz = null;
            List<ResUnit> hsyjDqg = null;



            xsjfs = this.doubleArrayToUnit(forecast);
            //获取洪水调度数据

            double[] floodControlFlow = {0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,24.76,55.60,88.90,117.66,275.60,341.47,307.13,297.32,241.89,204.95,175.62,140.95,116.57,97.66,77.51,59.55,46.49,38.15,28.55,21.22,15.66,12.13,9.35,7.58,6.32,5.31,4.80,4.30,3.79,3.79,3.54,3.28,3.28,3.03,3.03,2.78,2.78,2.78,2.78,2.53,2.53,2.53,2.53,2.53,2.27,2.27,2.27,2.27,2.27,2.02,2.27,2.02,2.02};

            double[] floodControlStage = {940.00,939.99,939.97,939.96,939.95,939.93,939.92,939.91,939.89,939.88,939.87,939.85,939.84,939.82,939.81,939.82,939.91,940.04,940.25,940.66,941.07,941.35,941.57,942.56,942.90,942.72,942.67,942.36,942.15,941.97,941.73,941.57,941.42,941.25,941.10,940.98,940.86,940.72,940.61,940.53,940.48,940.44,940.41,940.39,940.38,940.37,940.36,940.36,940.36,940.35,940.35,940.35,940.34,940.34,940.34,940.34,940.34,940.34,940.34,940.34,940.34,940.34,940.34,940.33,940.33,940.33,940.33,940.33,940.33,940.33,940.33,940.33};


            skfhStage = this.doubleArrayToUnit(floodControlStage);
            skfhFlow = this.doubleArrayToUnit(floodControlFlow);


            //获取洪水演进数据
            double[] floodEvoluteLwz = {0.00,0.12,0.16,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.00,10.46,34.98,66.88,65.81,200.88,310.06,310.29,314.15,271.01,230.52,199.32,163.17,134.19,112.73,91.69,71.70,55.57,45.34,35.02,26.43,19.54,14.87,11.32,8.93,7.29,5.99,5.25,4.69,4.04,3.92,3.71,3.40,3.38,3.13,3.12,2.88,2.81,2.79,2.84,2.62,2.55,2.53,2.53,2.59,2.36,2.30,2.28,2.28,2.33,2.05,2.27,2.09};

            double[] floodEvoluteDqg = {0.00,0.05,0.08,0.10,0.12,0.13,0.14,0.15,0.16,0.16,0.16,0.16,0.17,0.17,0.17,0.17,0.17,0.17,0.17,0.00,0.00,0.00,7.71,0.00,36.71,136.21,186.82,246.51,265.58,265.66,260.89,242.56,219.62,198.43,176.31,152.59,129.10,110.32,92.71,76.85,62.61,50.78,40.79,32.70,26.28,21.01,17.00,13.91,11.24,9.40,7.99,6.75,5.97,5.19,4.76,4.24,3.85,3.57,3.50,3.24,3.05,2.91,2.81,2.87,2.72,2.60,2.51,2.45,2.54,2.27,2.41,2.30};



            hsyjLwz = this.doubleArrayToUnit(floodEvoluteLwz);
            hsyjDqg = this.doubleArrayToUnit(floodEvoluteDqg);


            LocalDateTime now = LocalDateTime.now();

            response.setUpdateTime(DateLocalUtils.parseTimeToStr(now.withMinute((now.getMinute()/5)*5).withSecond(0).withNano(0)));
            response.setFlag(1d);
            response.setXajfs(xsjfs);
            response.setSkfhStage(skfhStage);
            response.setSkfhFlow(skfhFlow);
            response.setHsyjLwz(hsyjLwz);
            response.setHsyjDqg(hsyjDqg);
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
