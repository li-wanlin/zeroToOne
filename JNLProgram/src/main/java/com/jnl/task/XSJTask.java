package com.jnl.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnl.entity.PowerDay;
import com.jnl.entity.PowerHour;
import com.jnl.entity.XSJData;
import com.jnl.mapper.PowerDayMapper;
import com.jnl.mapper.PowerHourMapper;
import com.jnl.mapper.XSJDataMapper;
import com.jnl.service.impl.PowerDayServiceImpl;
import com.jnl.service.impl.PowerHourServiceImpl;
import com.jnl.service.impl.XSJDataServiceImpl;
import com.jnl.utils.ConvertLocalUtils;
import com.jnl.utils.DateLocalUtils;
import com.jnl.utils.HttpLocalUtils;
import com.jnl.vo.xsjVo.XSJInsertVo;
import com.jnl.vo.xsjVo.OverviewXSJ;
import com.jnl.vo.xsjVo.ReceiveXSJ;
import com.jnl.vo.xsjVo.StatisticXSJ;
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
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;


//@Component
public class XSJTask {

    @Resource
    XSJDataMapper xsjDataMapper;

    @Resource
    XSJDataServiceImpl xsjDataService;

    @Resource
    PowerHourMapper powerHourMapper;

    @Resource
    PowerHourServiceImpl powerHourService;


    @Resource
    PowerDayMapper powerDayMapper;


    @Resource
    PowerDayServiceImpl powerDayService;


    private static final Logger logger = LoggerFactory.getLogger(XSJTask.class);

    private static final String apiPrefix = "http://120.194.40.222:9090/XSJ-1300X/statistics/";
    private static final String apiSuffix = "/0/queryReport";
    private static final String stage = "0601005210";   //水位
    private static final String powerSum = "0601005003";  //总出力
    private static final String powerOne = "0601005186";  //1号机组出力
    private static final String powerTwo = "0601005194";  //2号机组出力
    private static final String powerThree = "0601005202";  //3号机组出力



    @Scheduled(cron = "10 0/5 * * * ?")
    @Async("asyncExecutor")
    public void XSJAPI(){
        //http://120.194.40.222:9090/XSJ-1300X/statistics/0601005210,0601005003,0601005051,0601005065,0601005080/2025-03-19/0/queryReport
        //当前日期
        String nowDay = DateLocalUtils.getGiveFormatNow("yyyy-MM-dd");
        //完整HTTP路径
        String apiComplete = apiPrefix + stage + "," + powerSum + "," + powerOne + "," + powerTwo + "," + powerThree + "/" + nowDay + apiSuffix;
        String response = HttpLocalUtils.sendGetRequest(apiComplete);
        ReceiveXSJ receiveXSJ = JSON.parseObject(response, ReceiveXSJ.class);
        if (receiveXSJ == null || receiveXSJ.getStatus() != 200 || receiveXSJ.getData().size() ==0){
            return;
        }
        long nowMilli = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        OverviewXSJ overviewXSJ;
        //原始statisticXSJ列表
        List<StatisticXSJ> statisticXSJList;
        //筛选后stastisticXSJ列表
        List<StatisticXSJ> eligibleList;
        StatisticXSJ lastStatisticXSJ;
        XSJInsertVo xsjInsertVo = new XSJInsertVo();

        for (int i = 0; i < receiveXSJ.getData().size(); i++) {
            overviewXSJ = receiveXSJ.getData().get(i);
            statisticXSJList = overviewXSJ.getStatistics();
            //获取当天当前时间前的统计数据-倒序
            eligibleList = statisticXSJList.stream()
                    .filter(statisticXSJ -> statisticXSJ.getRealTimeValue() != null)
                    .filter(statisticXSJ -> statisticXSJ.getSec() * 100 < nowMilli)
                    .sorted(Comparator.comparing(StatisticXSJ::getSec).reversed())
                    .collect(Collectors.toList());

            if (eligibleList.size() == 0){
                continue;
            }

            //获取当天最新统计数据
            lastStatisticXSJ = eligibleList.get(0);
            xsjInsertVo.setSec(lastStatisticXSJ.getSec());
            xsjInsertVo.setTime(DateLocalUtils.parseLongToDate(lastStatisticXSJ.getSec() * 1000));

            switch(lastStatisticXSJ.getMeasureId()){
                case stage:
                    xsjInsertVo.setStage(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerSum:
                    xsjInsertVo.setPowerSum(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerOne:
                    xsjInsertVo.setPowerOne(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerTwo:
                    xsjInsertVo.setPowerTwo(lastStatisticXSJ.getRealTimeValue());
                    break;
                case powerThree:
                    xsjInsertVo.setPowerThree(lastStatisticXSJ.getRealTimeValue());
                    break;
                default:
                    break;
            }

        }
        if (xsjInsertVo.getTime() == null){
            return;
        }
        XSJData xsjData = ConvertLocalUtils.toXSJData(xsjInsertVo);
        boolean save = xsjDataService.save(xsjData);


    }



    @Scheduled(cron = "0 5 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 5)
    @Async("asyncExecutor")
    public void tranPowerHour(){
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusHours(1).withMinute(0).withSecond(0).withNano(0);

            QueryWrapper<XSJData> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,now);
            queryWrapper.orderByAsc("time");

            List<XSJData> xsjDatas = xsjDataMapper.selectList(queryWrapper);
            if (xsjDatas == null || xsjDatas.size() == 0){
                return;
            }

            List<XSJData> agoHourData = xsjDatas.stream()
                    .filter(xsjData -> xsjData.getTime().compareTo(DateLocalUtils.parseTimeToDate(start.plusHours(1))) < 0)
                    .filter(xsjData -> xsjData.getPowerSum() != null)
                    .collect(Collectors.toList());


            List<XSJData> nowHourData = xsjDatas.stream()
                    .filter(xsjData -> xsjData.getTime().compareTo(DateLocalUtils.parseTimeToDate(start.plusHours(1))) >= 0)
                    .collect(Collectors.toList());



            //计算上小时数据
            if (agoHourData.size() > 0){
                double sum = 0d;
                int count = 0;
                XSJData agoXSJ = agoHourData.get(0);
                for (XSJData xsj:agoHourData) {
                    sum = sum + xsj.getPowerSum();
                    count = count + 1;
                }

                PowerHour agoPower = new PowerHour();
                agoPower.setUpdateTime(DateLocalUtils.parseTimeToDate(start));
                agoPower.setPowerOne(agoXSJ.getPowerOne() == null?null: BigDecimal.valueOf(agoXSJ.getPowerOne()).setScale(2, RoundingMode.DOWN).doubleValue());
                agoPower.setPowerTwo(agoXSJ.getPowerTwo() == null?null: BigDecimal.valueOf(agoXSJ.getPowerTwo()).setScale(2, RoundingMode.DOWN).doubleValue());
                agoPower.setPowerThree(agoXSJ.getPowerThree() == null?null: BigDecimal.valueOf(agoXSJ.getPowerThree()).setScale(2, RoundingMode.DOWN).doubleValue());
                agoPower.setPowerSum(agoXSJ.getPowerSum() == null?null: BigDecimal.valueOf(agoXSJ.getPowerSum()).setScale(2, RoundingMode.DOWN).doubleValue());
                agoPower.setElectricSum(agoXSJ.getPowerSum() == null?null: BigDecimal.valueOf(sum/count).setScale(2, RoundingMode.DOWN).doubleValue());

                powerHourService.saveOrUpdate(agoPower,new LambdaUpdateWrapper<PowerHour>()
                        .eq(PowerHour::getUpdateTime,agoPower.getUpdateTime()));
            }


            //计算本小时数据
            if (nowHourData.size() > 0){
                XSJData xsjData = nowHourData.get(0);

                PowerHour powerHour = new PowerHour();
                powerHour.setUpdateTime(DateLocalUtils.parseTimeToDate(start.plusHours(1)));
                powerHour.setElectricSum(-999d);
                powerHour.setPowerOne(xsjData.getPowerOne() == null?null: BigDecimal.valueOf(xsjData.getPowerOne()).setScale(2, RoundingMode.DOWN).doubleValue());
                powerHour.setPowerTwo(xsjData.getPowerTwo() == null?null: BigDecimal.valueOf(xsjData.getPowerTwo()).setScale(2, RoundingMode.DOWN).doubleValue());
                powerHour.setPowerThree(xsjData.getPowerThree() == null?null: BigDecimal.valueOf(xsjData.getPowerThree()).setScale(2, RoundingMode.DOWN).doubleValue());
                powerHour.setPowerSum(xsjData.getPowerSum() == null?null: BigDecimal.valueOf(xsjData.getPowerSum()).setScale(2, RoundingMode.DOWN).doubleValue());



                powerHourService.saveOrUpdate(powerHour,new LambdaUpdateWrapper<PowerHour>()
                        .eq(PowerHour::getUpdateTime,powerHour.getUpdateTime()));
            }

        }catch (Exception e){
            logger.error("电站小时级报表更新发生异常",e);
        }
    }


    //@Scheduled(fixedRate = 1000 * 60 * 5)
    @Scheduled(cron = "0 8 * * * ?")
    @Async("asyncExecutor")
    public void tranPowerDay(){
        try {
            LocalDate now = LocalDate.now();
            int hour = LocalDateTime.now().getHour();
            if (hour == 0){
                now = now.minusDays(1);
            }

            Date start = DateLocalUtils.parseLocalDateToDateStart(now);
            Date end = DateLocalUtils.parseLocalDateToDateEnd(now);


            QueryWrapper<PowerHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);

            List<PowerHour> powerHours = powerHourMapper.selectList(queryWrapper);
            if (powerHours == null || powerHours.size() == 0){
                return;
            }

            //2025.07.25：月报表取一日的平均值，总发电量取总和

            OptionalDouble powerOne = powerHours.stream().filter(powerHour -> powerHour.getPowerOne() != null)
                    .mapToDouble(PowerHour::getPowerOne)
                    .average();
                    //.reduce(Double::sum);

            OptionalDouble powerTwo = powerHours.stream().filter(powerHour -> powerHour.getPowerTwo() != null)
                    .mapToDouble(PowerHour::getPowerTwo)
                    .average();
            //.reduce(Double::sum);

            OptionalDouble powerThree = powerHours.stream().filter(powerHour -> powerHour.getPowerThree() != null)
                    .mapToDouble(PowerHour::getPowerThree)
                    .average();
            //.reduce(Double::sum);

            OptionalDouble powerSum = powerHours.stream().filter(powerHour -> powerHour.getPowerSum() != null)
                    .mapToDouble(PowerHour::getPowerSum)
                    .average();
            //.reduce(Double::sum);

            OptionalDouble electricSum = powerHours.stream().filter(powerHour -> powerHour.getElectricSum() != null && powerHour.getElectricSum() != -999)
                    .mapToDouble(PowerHour::getElectricSum)
                    .reduce(Double::sum);


            PowerDay powerDay = new PowerDay();
            powerDay.setUpdateTime(start);
            powerDay.setPowerOne(powerOne.isPresent() ? powerOne.getAsDouble() : null);
            powerDay.setPowerTwo(powerTwo.isPresent() ? powerTwo.getAsDouble() : null);
            powerDay.setPowerThree(powerThree.isPresent() ? powerThree.getAsDouble() : null);
            powerDay.setPowerSum(powerSum.isPresent() ? powerSum.getAsDouble() : null);
            powerDay.setElectricSum(electricSum.isPresent() ? electricSum.getAsDouble() : null);

            powerDayService.saveOrUpdate(powerDay,new LambdaUpdateWrapper<PowerDay>()
                    .eq(PowerDay::getUpdateTime,powerDay.getUpdateTime()));




        }catch (Exception e){
            logger.error("电站天级级报表更新发生异常",e);
        }
    }







}
