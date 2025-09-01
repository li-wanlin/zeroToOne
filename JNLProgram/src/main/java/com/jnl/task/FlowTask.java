package com.jnl.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnl.entity.*;
import com.jnl.mapper.AverageFlowMapper;
import com.jnl.mapper.HistoryFlowMapper;
import com.jnl.mapper.RealTimeFlowMapper;
import com.jnl.service.impl.FlowDayServiceImpl;
import com.jnl.service.impl.FlowHourServiceImpl;
import com.jnl.utils.DateLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


//@Component
public class FlowTask {

    @Resource
    FlowHourServiceImpl flowHourService;

    @Resource
    FlowDayServiceImpl flowDayService;


    @Resource
    RealTimeFlowMapper realTimeFlowMapper;


    @Resource
    AverageFlowMapper averageFlowMapper;


    @Resource
    HistoryFlowMapper historyFlowMapper;



    private static final Logger logger = LoggerFactory.getLogger(FlowTask.class);


    @Scheduled(cron = "59 0/5 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranFlowHour(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.minusHours(1).withMinute(0).withSecond(0).withNano(0);

            //获取发电、泄洪流量数据
            QueryWrapper<RealTimeFlow> realQuery = new QueryWrapper<>();
            realQuery.between("date_time",startTime,now);

            List<RealTimeFlow> realTimeFlows = realTimeFlowMapper.selectList(realQuery);
            if (realTimeFlows == null || realTimeFlows.size() ==0){
                return;
            }


            //获取平均来流量数据
            QueryWrapper<AverageFlow> averQuerywrapper = new QueryWrapper<>();
            averQuerywrapper.between("time",startTime,now);
            averQuerywrapper.orderByDesc("time");

            List<AverageFlow> averageFlows = averageFlowMapper.selectList(averQuerywrapper);


            List<RealTimeFlow> agoData = realTimeFlows.stream()
                    .filter(realTimeFlow -> realTimeFlow.getDateTime().compareTo(DateLocalUtils.parseTimeToDate(startTime.plusHours(1))) < 0)
                    .collect(Collectors.toList());


            List<RealTimeFlow> nowData = realTimeFlows.stream()
                    .filter(realTimeFlow -> realTimeFlow.getDateTime().compareTo(DateLocalUtils.parseTimeToDate(startTime.plusHours(1))) >= 0)
                    .collect(Collectors.toList());


            //上小时计算
            if (agoData.size() > 0 && averageFlows != null && averageFlows.size() > 0){
                List<Double> agoPowers = agoData.stream().filter(realTimeFlow -> realTimeFlow.getPower() != null)
                        .map(RealTimeFlow::getPower)
                        .collect(Collectors.toList());

                List<Double> agoFloods = agoData.stream().filter(realTimeFlow -> realTimeFlow.getFlood() != null)
                        .map(RealTimeFlow::getFlood)
                        .collect(Collectors.toList());


                AverageFlow averageFlow = averageFlows.get(0);


                double agoPowerNum = 0d;
                double agoFloodNum = 0d;
                double agoPower = 0d;
                double agoFlood = 0d;

                for (Double num1:agoPowers) {
                    agoPowerNum = agoPowerNum + num1;
                }

                for (Double num2:agoFloods) {
                    agoFloodNum = agoFloodNum + num2;
                }

                if (agoPowerNum > 0){
                    agoPower = agoPowerNum/agoPowers.size();
                }

                if (agoFloodNum > 0){
                    agoFlood = agoFloodNum/agoFloods.size();
                }


                FlowHour agoFlowHour = new FlowHour();
                agoFlowHour.setUpdateTime(DateLocalUtils.parseTimeToDate(startTime));


                agoFlowHour.setAverFlow(BigDecimal.valueOf(averageFlow.getFlow()).setScale(2, RoundingMode.DOWN).doubleValue());
                agoFlowHour.setPower(BigDecimal.valueOf(agoPower).setScale(2, RoundingMode.DOWN).doubleValue());
                agoFlowHour.setFlood(BigDecimal.valueOf(agoFlood).setScale(2, RoundingMode.DOWN).doubleValue());


                flowHourService.saveOrUpdate(agoFlowHour,new LambdaUpdateWrapper<FlowHour>()
                        .eq(FlowHour::getUpdateTime,agoFlowHour.getUpdateTime()));
            }



            //计算本小时
            if (nowData.size() > 0){
                List<Double> powers = nowData.stream().filter(realTimeFlow -> realTimeFlow.getPower() != null)
                        .map(RealTimeFlow::getPower)
                        .collect(Collectors.toList());

                List<Double> floods = nowData.stream().filter(realTimeFlow -> realTimeFlow.getFlood() != null)
                        .map(RealTimeFlow::getFlood)
                        .collect(Collectors.toList());


                double powerNum = 0d;
                double floodNum = 0d;
                double power = 0d;
                double flood = 0d;

                for (Double num1:powers) {
                    powerNum = powerNum + num1;
                }

                for (Double num2:floods) {
                    floodNum = floodNum + num2;
                }

                if (powerNum > 0){
                    power = powerNum/powers.size();
                }

                if (floodNum > 0){
                    flood = floodNum/floods.size();
                }

                FlowHour flowHour = new FlowHour();
                flowHour.setUpdateTime(DateLocalUtils.parseTimeToDate(startTime.plusHours(1)));

                flowHour.setAverFlow(-999d);
                flowHour.setPower(BigDecimal.valueOf(power).setScale(2, RoundingMode.DOWN).doubleValue());
                flowHour.setFlood(BigDecimal.valueOf(flood).setScale(2, RoundingMode.DOWN).doubleValue());


                flowHourService.saveOrUpdate(flowHour,new LambdaUpdateWrapper<FlowHour>()
                        .eq(FlowHour::getUpdateTime,flowHour.getUpdateTime()));
            }

            logger.info("流量小时级数据更新成功");


        }catch (Exception e){
            logger.error("流量小时级数据更新发生异常",e);
        }
    }



    @Scheduled(cron = "0 3 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranFlowDay(){
        try {
            LocalDate nowDay = LocalDate.now();
            LocalDate startDay = nowDay.minusDays(1);


            Date agoStartTime = DateLocalUtils.parseLocalDateToDateStart(startDay);
            Date nowStartTime = DateLocalUtils.parseLocalDateToDateStart(nowDay);

            Date nowEndTime = DateLocalUtils.parseLocalDateToDateEnd(nowDay);

            //获取发电、泄洪流量数据
            QueryWrapper<RealTimeFlow> realQuery = new QueryWrapper<>();
            realQuery.between("date_time",agoStartTime,nowEndTime);

            List<RealTimeFlow> realTimeFlows = realTimeFlowMapper.selectList(realQuery);
            if (realTimeFlows == null || realTimeFlows.size() ==0){
                return;
            }


            //获取平均来流量数据
            QueryWrapper<HistoryFlow> hfQuery = new QueryWrapper<>();
            hfQuery.between("time",startDay,nowDay);
            hfQuery.orderByDesc("time");

            List<HistoryFlow> historyFlows = historyFlowMapper.selectList(hfQuery);


            List<RealTimeFlow> agoData = realTimeFlows.stream()
                    .filter(realTimeFlow -> realTimeFlow.getDateTime().compareTo(nowStartTime) < 0)
                    .collect(Collectors.toList());


            List<RealTimeFlow> nowData = realTimeFlows.stream()
                    .filter(realTimeFlow -> realTimeFlow.getDateTime().compareTo(nowStartTime) >= 0)
                    .collect(Collectors.toList());


            //计算上一天数据
            if (agoData.size() > 0 && historyFlows != null && historyFlows.size() > 0){

                List<Double> agoPowers = agoData.stream().filter(realTimeFlow -> realTimeFlow.getPower() != null)
                        .map(RealTimeFlow::getPower)
                        .collect(Collectors.toList());

                List<Double> agoFloods = agoData.stream().filter(realTimeFlow -> realTimeFlow.getFlood() != null)
                        .map(RealTimeFlow::getFlood)
                        .collect(Collectors.toList());


                double agoPowerNum = 0d;
                double agoFloodNum = 0d;
                double agoPower = 0d;
                double agoFlood = 0d;

                for (Double num1:agoPowers) {
                    agoPowerNum = agoPowerNum + num1;
                }

                for (Double num2:agoFloods) {
                    agoFloodNum = agoFloodNum + num2;
                }

                if (agoPowerNum > 0){
                    agoPower = agoPowerNum/agoPowers.size();
                }

                if (agoFloodNum > 0){
                    agoFlood = agoFloodNum/agoFloods.size();
                }


                HistoryFlow historyFlow = historyFlows.get(0);
                FlowDay agoFlowDay = new FlowDay();
                agoFlowDay.setUpdateTime(agoStartTime);
                agoFlowDay.setAverFlow(BigDecimal.valueOf(historyFlow.getFlow()).setScale(2, RoundingMode.DOWN).doubleValue());
                agoFlowDay.setPower(BigDecimal.valueOf(agoPower).setScale(2, RoundingMode.DOWN).doubleValue());
                agoFlowDay.setFlood(BigDecimal.valueOf(agoFlood).setScale(2, RoundingMode.DOWN).doubleValue());


                flowDayService.saveOrUpdate(agoFlowDay,new LambdaUpdateWrapper<FlowDay>()
                        .eq(FlowDay::getUpdateTime,agoFlowDay.getUpdateTime()));

            }


            //计算今天数据
            if (nowData.size() > 0){
                List<Double> powers = realTimeFlows.stream().filter(realTimeFlow -> realTimeFlow.getPower() != null)
                        .map(RealTimeFlow::getPower)
                        .collect(Collectors.toList());

                List<Double> floods = realTimeFlows.stream().filter(realTimeFlow -> realTimeFlow.getFlood() != null)
                        .map(RealTimeFlow::getFlood)
                        .collect(Collectors.toList());


                double powerNum = 0d;
                double floodNum = 0d;
                double power = 0d;
                double flood = 0d;

                for (Double num1:powers) {
                    powerNum = powerNum + num1;
                }

                for (Double num2:floods) {
                    floodNum = floodNum + num2;
                }

                if (powerNum > 0){
                    power = powerNum/powers.size();
                }

                if (floodNum > 0){
                    flood = floodNum/floods.size();
                }


                FlowDay flowDay = new FlowDay();
                flowDay.setUpdateTime(nowStartTime);
                flowDay.setAverFlow(-999d);
                flowDay.setPower(BigDecimal.valueOf(power).setScale(2, RoundingMode.DOWN).doubleValue());
                flowDay.setFlood(BigDecimal.valueOf(flood).setScale(2, RoundingMode.DOWN).doubleValue());


                flowDayService.saveOrUpdate(flowDay,new LambdaUpdateWrapper<FlowDay>()
                        .eq(FlowDay::getUpdateTime,flowDay.getUpdateTime()));
            }

            logger.info("流量天级数据更新成功");

        }catch (Exception e){
            logger.error("流量天级数据更新发生异常",e);
        }
    }






}
