/*
package com.jnl.sevice.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jnl.entity.*;
import com.jnl.manage.MatlabEngineManager;
import com.jnl.mapper.*;
import com.jnl.sevice.DamSignService;
import com.jnl.sevice.RealTimeFlowService;
import com.jnl.task.RealTimeDataTask;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.damSignVo.SignUnit;
import com.jnl.vo.fourPreventVo.RainData;
import com.jnl.vo.fourPreventVo.RainRangeUnit;
import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import hs1.Class1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import skw2.Skw2MCRFactory;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Service
public class MatlabServiceImpl {


    @Resource
    RainFallDataServiceImpl rainFallDataService;


    @Resource
    XSJDataMapper xsjDataMapper;


    @Resource
    MatlabEngineManager matlabEngineManager;


    @Resource
    DamSignServiceImpl damSignService;



    @Resource
    RealTimeFlowServiceImpl realTimeFlowService;


    @Resource
    HistoryFlowServiceImpl historyFlowService;

    @Resource
    HistoryFlowMapper historyFlowMapper;

    @Resource
    ForecastFlowMapper forecastFlowMapper;

    @Resource
    ForecastFlowServiceImpl forecastFlowService;

    @Resource
    WeatherServiceImpl weatherService;


    @Resource
    AverageFlowMapper averageFlowMapper;

    @Resource
    AverageFlowServiceImpl averageFlowService;

    @Resource
    FloodForecastMapper floodForecastMapper;

    @Resource
    FloodForecastServiceImpl floodForecastService;


    @Resource
    FourPreventServiceImpl fourPreventService;


    @Resource
    FloodControlServiceImpl floodControlService;

    @Resource
    FloodControlMapper floodControlMapper;


    @Resource
    FloodEvoluteServiceImpl floodEvoluteService;








    private static final Logger logger = LoggerFactory.getLogger(MatlabServiceImpl.class);


    private static final DecimalFormat decimalFormat = new DecimalFormat("###0.00");// 格式化设置
    private static final DecimalFormat decimalFormat1 = new DecimalFormat("###0");// 格式化设置
    private static final DecimalFormat decimalFormat2 = new DecimalFormat("###0.0000");// 格式化设置






    */
/**
     * 调用hs1.jar的计算方法
     *//*

    public Map<String, Object> calculateHs1(double P81, double[] P82) {
        long startTime = System.currentTimeMillis();
        Object[] result8 = null;
        MWNumericArray output801 = null;
        MWNumericArray output802 = null;

        try {
            // 获取单例引擎
            Class1 engine = matlabEngineManager.getEngine( "hs1.jar");



            // MATLAB计算
            result8 = engine.hs1(2, P81, P82);

            // 数据类型转换
            output801 = (MWNumericArray) result8[0];
            output802 = (MWNumericArray) result8[1];

            double res801 = output801.getDouble();
            double[] res802 = (double[]) output802.toDoubleArray();

            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalRate", decimalFormat.format(res801));
            result.put("unitRates", formatArray(res802));
            result.put("executionTime", System.currentTimeMillis() - startTime);
            return result;

        } catch (MWException e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            MWArray.disposeArray(output801);
            MWArray.disposeArray(output802);
            MWArray.disposeArray(result8);
        }
    }


    */
/**
     * 调用skw2.jar的计算方法，计算实时发电流量、实时泄洪流量
     *//*

    public void calculateSkw2() {
        Object[] result = null;
        MWNumericArray output = null;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime insert = now.withMinute((now.getMinute()/5)*5);

        try {

            //获取塌坝标志位
            SignUnit signUnit = damSignService.selectEnable();
            if (signUnit == null || signUnit.getSign() == null){
                return ;
            }

            //获取新世纪数据
            QueryWrapper<XSJData> xsjWrapper = new QueryWrapper<>();
            LocalDateTime start = now.minusMinutes(10);
            xsjWrapper.between("time",start,now);
            xsjWrapper.orderByDesc("time");
            List<XSJData> xsjDatas = xsjDataMapper.selectList(xsjWrapper);
            if (xsjDatas == null || xsjDatas.size() == 0){
                return ;
            }

            Double Hs1 = xsjDatas.get(0).getStage();              //实时水位
            Double N1 = xsjDatas.get(0).getPowerOne();        //1号机组实时出力
            Double N2 = xsjDatas.get(0).getPowerTwo();        //2号机组实时出力
            Double N3 = xsjDatas.get(0).getPowerThree();    //3号机组实时出力
            Integer T = Integer.parseInt(signUnit.getSign());       //橡胶坝塌坝标志


            // 获取单例引擎
            skw2.Class1 engine = matlabEngineManager.getEngine("skw2.jar");



            // MATLAB计算
            result = engine.skw2(1, Hs1, N1, N2, N3, T);




            // 数据类型转换
            output = (MWNumericArray) result[0];
            double outputDouble = output.getDouble(1);
            double outputDouble2 = output.getDouble(2);


            RealTimeFlow realTimeFlow = new RealTimeFlow();
            realTimeFlow.setDateTime(DateLocalUtils.parseTimeToDate(insert));
            realTimeFlow.setPower(Double.parseDouble(decimalFormat.format(outputDouble)));
            realTimeFlow.setFlood(Double.parseDouble(decimalFormat.format(outputDouble2)));

            realTimeFlowService.save(realTimeFlow);

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }





    */
/**
     * 调用sksw1.jar的计算方法，计算昨日平均来流量
     *//*

    public void calculateSksw1() {
        Object[] result = null;
        MWNumericArray output = null;
        LocalDateTime now = LocalDateTime.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

        try {


            //查询昨天是否由数据，有数据则不向下进行
            QueryWrapper<HistoryFlow> historyWrapper = new QueryWrapper<>();
            historyWrapper.eq("time",yesterday);
            List<HistoryFlow> historyFlows = historyFlowMapper.selectList(historyWrapper);

            if (historyFlows != null && historyFlows.size() > 0){
                return;
            }



            //获取新世纪数据
            QueryWrapper<XSJData> xsjWrapper = new QueryWrapper<>();
            xsjWrapper.between("time",start,end);
            xsjWrapper.orderByAsc("time");
            List<XSJData> xsjDatas = xsjDataMapper.selectList(xsjWrapper);
            if (xsjDatas == null || xsjDatas.size() < 2){
                return;
            }


            RealTimeFlow average = realTimeFlowService.averageCal();
            if (average == null){
                return;
            }

            Double Hs1 = xsjDatas.get(xsjDatas.size() - 1).getStage();
            Double Hs2 = xsjDatas.get(0).getStage();
            Double Qd = average.getAveragePower();
            Double Qh = average.getAverageFlood();



            // 获取单例引擎
            sksw1.Class1 engine = matlabEngineManager.getEngine("sksw1.jar");



            // MATLAB计算
            result = engine.sksw1(1, Hs1, Hs2, Qd, Qh);




            // 数据类型转换
            output = (MWNumericArray) result[0];
            double outputDouble = output.getDouble(1);


            HistoryFlow historyFlow = new HistoryFlow();
            historyFlow.setTime(DateLocalUtils.parseTimeToDate(start));
            historyFlow.setFlow(Double.parseDouble(decimalFormat.format(outputDouble)));

            historyFlowService.save(historyFlow);

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }




    */
/**
     * 调用ryc.jar的计算方法,计算入库径流量
     *//*

    public void calculateRyc() {
        Object[] result = null;
        MWNumericArray output = null;
        LocalDate nowDay = LocalDate.now();


        try {


            this.calculateSksw1();

            Integer id = 0;
            Double fiveDayFlow;
            Double fourDayFlow;
            Double threeDayFlow;
            Double twoDayFlow;
            Double oneDayFlow;
            Double twoDayRain;
            Double oneDayRain;
            Double nowDayRain;
            Double forecast;


            QueryWrapper<ForecastFlow> forecastWrapper = new QueryWrapper<>();
            forecastWrapper.eq("time",nowDay);
            List<ForecastFlow> forecastFlows = forecastFlowMapper.selectList(forecastWrapper);
            if (forecastFlows != null && forecastFlows.size() > 0){
                //如果当日有值，只需更新
                id = forecastFlows.get(0).getId();
                fiveDayFlow = forecastFlows.get(0).getFiveDayFlow();
                fourDayFlow = forecastFlows.get(0).getFourDayFlow();
                threeDayFlow = forecastFlows.get(0).getThreeDayFlow();
                twoDayFlow = forecastFlows.get(0).getTwoDayFlow();
                oneDayFlow = forecastFlows.get(0).getOneDayFlow();
                twoDayRain = forecastFlows.get(0).getTwoDayRain();
                oneDayRain = forecastFlows.get(0).getOneDayRain();
            }else {
                QueryWrapper<AverageFlow> averageWrapper = new QueryWrapper<>();
                averageWrapper.between("time",nowDay.minusDays(5),nowDay);
                averageWrapper.orderByAsc("time");
                List<AverageFlow> averageFlows = averageFlowMapper.selectList(averageWrapper);
                if (averageFlows == null || averageFlows.size() == 0){
                    return;
                }


                Map<String, Double> rainData = rainFallDataService.rainFallAverage();
                if (rainData == null){
                    return;
                }


                fiveDayFlow = averageFlows.stream()
                        .filter(averageFlow -> DateLocalUtils.parseLocalDateToDate(nowDay.minusDays(5)).equals(averageFlow.getTime()))
                        .mapToDouble(AverageFlow::getFlow)
                        .average().orElse(0.0);

                fourDayFlow = averageFlows.stream()
                        .filter(averageFlow -> DateLocalUtils.parseLocalDateToDate(nowDay.minusDays(4)).equals(averageFlow.getTime()))
                        .mapToDouble(AverageFlow::getFlow)
                        .average().orElse(0.0);

                threeDayFlow = averageFlows.stream()
                        .filter(averageFlow -> DateLocalUtils.parseLocalDateToDate(nowDay.minusDays(3)).equals(averageFlow.getTime()))
                        .mapToDouble(AverageFlow::getFlow)
                        .average().orElse(0.0);

                twoDayFlow = averageFlows.stream()
                        .filter(averageFlow -> DateLocalUtils.parseLocalDateToDate(nowDay.minusDays(2)).equals(averageFlow.getTime()))
                        .mapToDouble(AverageFlow::getFlow)
                        .average().orElse(0.0);

                oneDayFlow = averageFlows.stream()
                        .filter(averageFlow -> DateLocalUtils.parseLocalDateToDate(nowDay.minusDays(1)).equals(averageFlow.getTime()))
                        .mapToDouble(AverageFlow::getFlow)
                        .average().orElse(0.0);


                twoDayRain = rainData.get("twoDayRain");
                oneDayRain = rainData.get("oneDayRain");

            }

            Map<String, Double> weatherData = weatherService.calAverageCall();
            if (weatherData == null){
                return;
            }

            nowDayRain = weatherData.get("nowDayRain");

            Double b[] = {fiveDayFlow,fourDayFlow,threeDayFlow,twoDayFlow,oneDayFlow};



            // 获取单例引擎
            ryc.Class1 engine = matlabEngineManager.getEngine( "ryc.jar");



            // MATLAB计算
            result = engine.ryc(1, b, twoDayRain,oneDayRain,nowDayRain);

            // 数据类型转换
            output = (MWNumericArray) result[0];

            forecast = output.getDouble();


            ForecastFlow forecastFlow = new ForecastFlow();
            forecastFlow.setFiveDayFlow(fiveDayFlow);
            forecastFlow.setFourDayFlow(fourDayFlow);
            forecastFlow.setThreeDayFlow(threeDayFlow);
            forecastFlow.setTwoDayFlow(twoDayFlow);
            forecastFlow.setOneDayFlow(oneDayFlow);
            forecastFlow.setTwoDayRain(twoDayRain);
            forecastFlow.setOneDayRain(oneDayRain);
            forecastFlow.setNowRain(nowDayRain);
            forecastFlow.setForecast(Double.parseDouble(decimalFormat.format(forecast)));
            forecastFlow.setTime(DateLocalUtils.parseLocalDateToDate(LocalDate.now()));

            if (id != 0){
                forecastFlow.setId(id);
                forecastFlowService.updateById(forecastFlow);
            }else {
                forecastFlowService.save(forecastFlow);
            }

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }


    */
/**
     * 调用skw1.jar的计算方法，计算小时平均来流量
     *//*

    public void calculateSkw1() {
        Object[] result = null;
        MWNumericArray output = null;
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime start = now.minusHours(1);

        try {
            QueryWrapper<AverageFlow> averageWrapper = new QueryWrapper<>();
            averageWrapper.eq("time",now);
            List<AverageFlow> averageFlows = averageFlowMapper.selectList(averageWrapper);
            if (averageFlows != null && averageFlows.size() > 0){
                return;
            }


            //获取塌坝标志位
            SignUnit signUnit = damSignService.selectEnable();
            if (signUnit == null || signUnit.getSign() == null){
                return ;
            }

            //获取新世纪数据
            QueryWrapper<XSJData> xsjWrapper = new QueryWrapper<>();

            xsjWrapper.between("time",start,now);
            xsjWrapper.orderByAsc("time");
            List<XSJData> xsjDatas = xsjDataMapper.selectList(xsjWrapper);
            if (xsjDatas == null || xsjDatas.size() == 0){
                return ;
            }



            Double Hs1 = xsjDatas.get(xsjDatas.size() - 1).getStage();              //当前整点时刻水位
            Double Hs2 = xsjDatas.get(0).getStage();                                //上个整点时刻水位

            Double N1 = xsjDatas.stream().filter(xsjData -> xsjData.getPowerOne() != null).mapToDouble(XSJData::getPowerOne)
                    .average().orElse(0.0);                                     //1号机组实时出力
            Double N2 = xsjDatas.stream().filter(xsjData -> xsjData.getPowerTwo() != null).mapToDouble(XSJData::getPowerTwo)
                    .average().orElse(0.0);                                     //2号机组实时出力
            Double N3 = xsjDatas.stream().filter(xsjData -> xsjData.getPowerThree() != null).mapToDouble(XSJData::getPowerThree)
                    .average().orElse(0.0);                                     //3号机组实时出力
            Integer T = Integer.parseInt(signUnit.getSign());                           //橡胶坝塌坝标志


            // 获取单例引擎
            skw1.Class1 engine = matlabEngineManager.getEngine("skw1.jar");



            // MATLAB计算
            result = engine.skw1(1, Hs1,Hs2,N1, N2, N3, T);




            // 数据类型转换
            output = (MWNumericArray) result[0];
            double outputDouble = output.getDouble(1);


            AverageFlow averageFlow = new AverageFlow();
            averageFlow.setTime(DateLocalUtils.parseTimeToDate(now));
            averageFlow.setFlow(Double.parseDouble(decimalFormat.format(outputDouble)));


            averageFlowService.save(averageFlow);

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }





    */
/**
     * 调用xsjf.jar的计算方法，计算洪水预报
     *//*

    public double[] calculateXsjf() {
        Object[] result = null;
        MWNumericArray output = null;
        MWNumericArray output1 = null;
        MWNumericArray output2 = null;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowHour = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime start = now.minusMinutes(10);


        try {



            List<RainRangeUnit> unitsSuf = weatherService.forecastThree();
            List<RainRangeUnit> unitsPre = rainFallDataService.dayAgo();


            if (unitsPre == null || unitsPre.size() == 0 || unitsSuf == null || unitsSuf.size() == 0){
                return null;
            }
            Double[] P1 = unitsPre.stream().map(RainRangeUnit::getValue).toArray(Double[]::new);
            Double[] P2 = unitsSuf.stream().map(RainRangeUnit::getValue).toArray(Double[]::new);




            // 获取单例引擎
            xajf.Class1 engine = matlabEngineManager.getEngine("xajf.jar");



            // MATLAB计算
            result = engine.xajf(3, P1, P2);




            // 数据类型转换
            output = (MWNumericArray) result[0];
            output1 = (MWNumericArray) result[1];
            output2 = (MWNumericArray) result[2];

            double res2[] = new double[2];
            for (int i = 0; i < 2; i++) {
                res2[i] = output1.getDouble(i+1);
            }

            double res3[] = new double[72];
            for (int i = 0; i < 72; i++) {
                res3[i] = output2.getDouble(i+1);
            }




            FloodForecast forecast = new FloodForecast();
            forecast.setTime(DateLocalUtils.parseTimeToDate(now.withMinute((now.getMinute()/5)*5).withSecond(0).withNano(0)));
            forecast.setFlag(Double.parseDouble(decimalFormat.format(output.getDouble(1))));
            forecast.setMaxFlood(Double.parseDouble(decimalFormat.format(res2[0])));
            forecast.setAppear(DateLocalUtils.parseTimeToDate(nowHour.plusHours((long)res2[1])));
            forecast.setFlow(JSON.toJSONString(res3));


            floodForecastService.save(forecast);
            return res3;

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            if (output1 != null) {
                output1.dispose(); // 或 MWArray.disposeArray(output);
            }

            if (output2 != null) {
                output2.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }




    */
/**
     * 调用skfh.jar的计算方法，洪水调度计算
     *//*

    public double[] calculateSkfh(double[] P2) {
        Object[] result = null;
        MWNumericArray output = null;
        MWNumericArray output2 = null;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(10);

        try {
*/
/*            //有数据则不更新
            QueryWrapper<FloodControl> averageWrapper = new QueryWrapper<>();
            averageWrapper.between("time",start,now);
            List<FloodControl> floodControls = floodControlMapper.selectList(averageWrapper);
            if (floodControls != null && floodControls.size() > 0){
                return;
            }*//*


            if (P2 == null){
                return null;
            }


            //获取新世纪数据
            QueryWrapper<XSJData> xsjWrapper = new QueryWrapper<>();

            xsjWrapper.between("time",start,now);
            xsjWrapper.orderByDesc("time");
            List<XSJData> xsjDatas = xsjDataMapper.selectList(xsjWrapper);
            if (xsjDatas == null || xsjDatas.size() == 0){
                return null;
            }



            Double Zc = xsjDatas.get(0).getStage();



            // 获取单例引擎
            skfh.Class1 engine = matlabEngineManager.getEngine("skfh.jar");



            // MATLAB计算
            result = engine.skfh(2, Zc,P2);



            // 数据类型转换
            output = (MWNumericArray) result[0];
            output2 = (MWNumericArray) result[1];

            double res[] = new double[72];
            double res2[] = new double[72];

            for (int i = 0; i < 72; i++) {
                res[i] = output.getDouble(i+1);
            }

            for (int i = 0; i < 72; i++) {
                res2[i] = output2.getDouble(i+1);
            }


            FloodControl floodControl = new FloodControl();
            floodControl.setTime(DateLocalUtils.parseTimeToDate(now.withMinute((now.getMinute()/5)*5).withSecond(0).withNano(0)));
            floodControl.setStage(JSON.toJSONString(res));
            floodControl.setFlow(JSON.toJSONString(res2));


            floodControlService.save(floodControl);

            return res2;

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            if (output2 != null) {
                output2.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }





    */
/**
     * 调用hsyj.jar的计算方法，洪水演进计算
     *//*

    public void calculateHsyj() {
        Object[] result = null;
        MWNumericArray output = null;
        MWNumericArray output2 = null;
        MWNumericArray output3 = null;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusMinutes(10);
        LocalDateTime insert = now.withMinute((now.getMinute()/5)*5);

        try {

            double[] P2 = this.calculateXsjf();

            if (P2 == null){
                logger.info("洪水预报matlab计算出现问题");
                return;
            }

            double[] Q = this.calculateSkfh(P2);
            if (Q == null){
                logger.info("洪水调度matlab计算出现问题");
                return;
            }


            // 获取单例引擎
            hsyj.Class1 engine = matlabEngineManager.getEngine("hsyj.jar");



            // MATLAB计算
            result = engine.hsyj(3, Q);




            // 数据类型转换
            output = (MWNumericArray) result[0];
            output2 = (MWNumericArray) result[1];
            output3 = (MWNumericArray) result[2];


            double res[] = new double[72];
            double res2[] = new double[72];
            double res3[] = new double[72];



            //取出数值
            for (int i = 0; i < 72; i++) {
                res[i] = output.getDouble(i + 1);
            }

            for (int i = 0; i < 72; i++) {
                res2[i] = output2.getDouble(i + 1);
            }

            for (int i = 0; i < 72; i++) {
                res3[i] = output3.getDouble(i + 1);
            }


            FloodEvolute floodEvolute = new FloodEvolute();
            floodEvolute.setTime(DateLocalUtils.parseTimeToDate(insert));
            floodEvolute.setLwzFlow(JSON.toJSONString(res));
            floodEvolute.setDqgFlow(JSON.toJSONString(res2));
            floodEvolute.setBylFlow(JSON.toJSONString(res3));

            floodEvoluteService.save(floodEvolute);

        } catch (Exception e) {
            throw new RuntimeException("MATLAB计算失败", e);
        } finally {
            // 释放资源
            // 释放 output 对象
            if (output != null) {
                output.dispose(); // 或 MWArray.disposeArray(output);
            }

            if (output2 != null) {
                output2.dispose(); // 或 MWArray.disposeArray(output);
            }

            if (output3 != null) {
                output3.dispose(); // 或 MWArray.disposeArray(output);
            }

            // 释放 result 数组中的每个 MWArray 对象
            if (result != null) {
                for (Object obj : result) {
                    if (obj instanceof MWArray) {
                        ((MWArray) obj).dispose(); // 或 MWArray.disposeArray((MWArray) obj);
                    }
                }
            }
        }
    }








    private String[] formatArray(double[] array) {
        String[] formatted = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            formatted[i] = decimalFormat.format(array[i]);
        }
        return formatted;
    }


}*/
