package com.jnl.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnl.entity.OnenetDev7;
import com.jnl.entity.SeepageDay;
import com.jnl.entity.SeepageHour;
import com.jnl.entity.SeepageInfo;
import com.jnl.mapper.OnenetDev7Mapper;
import com.jnl.mapper.SeepageDayMapper;
import com.jnl.mapper.SeepageHourMapper;
import com.jnl.mapper.SeepageInfoMapper;
import com.jnl.service.impl.SafetyWarnServiceImpl;
import com.jnl.service.impl.SeepageDayServiceImpl;
import com.jnl.service.impl.SeepageHourServiceImpl;
import com.jnl.service.impl.SeepageInfoServiceImpl;
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
import java.util.OptionalDouble;

//@Component
public class SeepageTask {

    @Resource
    SeepageInfoServiceImpl seepageInfoService;

    @Resource
    SafetyWarnServiceImpl safetyWarnService;

    @Resource
    SeepageInfoMapper seepageInfoMapper;


    @Resource
    SeepageHourMapper seepageHourMapper;

    @Resource
    SeepageHourServiceImpl seepageHourService;


    @Resource
    SeepageDayMapper seepageDayMapper;

    @Resource
    SeepageDayServiceImpl seepageDayService;

    @Resource
    OnenetDev7Mapper onenetDev7Mapper;

    private static final Logger logger = LoggerFactory.getLogger(SeepageTask.class);



    @Scheduled(fixedRate = 1000 * 60)
    @Async("asyncExecutor")
    public void seepageCal(){
        try {

            LocalDateTime now = LocalDateTime.now().withNano(0);
            LocalDateTime nowMin = LocalDateTime.now().withSecond(0).withNano(0);
            LocalDateTime start = now.minusMinutes(5);
            QueryWrapper<OnenetDev7> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);
            queryWrapper.orderByDesc("update_time");
            List<OnenetDev7> infos = onenetDev7Mapper.selectList(queryWrapper);
            if (infos == null || infos.size() < 2){
                return;
            }
            Double inputSuf = infos.get(0).getLv() == null ? null :(double)infos.get(0).getLv();
            Double inputLv2 = infos.get(0).getLv2() == null ? null :(double)infos.get(0).getLv2();
            Double inputLv3 = infos.get(0).getLv3() == null ? null :(double)infos.get(0).getLv3();
            Double inputPre = infos.get(infos.size()-1).getLv() == null ? null : (double)infos.get(infos.size()-1).getLv();

            SeepageInfo seepageInfo = new SeepageInfo();
            seepageInfo.setUpdateTime(DateLocalUtils.parseTimeToDate(nowMin));


            Double lv = calLv(inputPre, inputSuf);
            Double lv2 = calLv2(inputLv2);
            Double lv3 = calLv3(inputLv3);


            seepageInfo.setLv(lv == null ? null : BigDecimal.valueOf(lv)
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue());
            seepageInfo.setLv2(lv2 == null ? null : BigDecimal.valueOf(lv2)
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue());
            seepageInfo.setLv3(lv3 == null ? null : BigDecimal.valueOf(lv3)
                    .setScale(2, RoundingMode.DOWN)
                    .doubleValue());


            safetyWarnService.saveOnenet(seepageInfo);


            seepageInfoService.save(seepageInfo);
        }catch (Exception e){
            logger.error("计算渗流量定时任务发生异常",e);
        }

    }


    @Scheduled(cron = "0 0 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranSeepageHour(){
        try {
            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime start = now.minusHours(1);

            QueryWrapper<SeepageInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,now);

            List<SeepageInfo> seepageInfos = seepageInfoMapper.selectList(queryWrapper);
            if (seepageInfos == null || seepageInfos.size() == 0){
                return;
            }

            OptionalDouble maxLv = seepageInfos.stream().filter(seepageInfo -> seepageInfo.getLv() != null)
                    .mapToDouble(SeepageInfo::getLv)
                    .max();

            OptionalDouble maxLv2 = seepageInfos.stream().filter(seepageInfo -> seepageInfo.getLv2() != null)
                    .mapToDouble(SeepageInfo::getLv2)
                    .max();

            OptionalDouble maxLv3 = seepageInfos.stream().filter(seepageInfo -> seepageInfo.getLv3() != null)
                    .mapToDouble(SeepageInfo::getLv3)
                    .max();



            SeepageHour seepageHour = new SeepageHour();
            seepageHour.setUpdateTime(DateLocalUtils.parseTimeToDate(now));
            seepageHour.setLv(maxLv.isPresent() ? maxLv.getAsDouble() : null);
            seepageHour.setLv2(maxLv2.isPresent() ? maxLv2.getAsDouble() : null);
            seepageHour.setLv3(maxLv3.isPresent() ? maxLv3.getAsDouble() : null);


            seepageHourService.saveOrUpdate(seepageHour,new LambdaUpdateWrapper<SeepageHour>()
                    .eq(SeepageHour::getUpdateTime,seepageHour.getUpdateTime()));
        }catch (Exception e){
            logger.error("渗流小时级报表更新发生异常",e);
        }
    }

    @Scheduled(cron = "0 3 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void tranSeepageDay(){
        try {
            LocalDate nowDate = LocalDate.now();
            Date start = DateLocalUtils.parseLocalDateToDateStart(nowDate);
            Date end = DateLocalUtils.parseLocalDateToDateEnd(nowDate);
            QueryWrapper<SeepageHour> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("update_time",start,end);
            List<SeepageHour> seepageHours = seepageHourMapper.selectList(queryWrapper);
            if (seepageHours == null || seepageHours.size() == 0){
                return;
            }

            OptionalDouble maxLv = seepageHours.stream().filter(seepageHour -> seepageHour.getLv() != null)
                    .mapToDouble(SeepageHour::getLv)
                    .max();

            OptionalDouble maxLv2 = seepageHours.stream().filter(seepageHour -> seepageHour.getLv2() != null)
                    .mapToDouble(SeepageHour::getLv2)
                    .max();

            OptionalDouble maxLv3 = seepageHours.stream().filter(seepageHour -> seepageHour.getLv3() != null)
                    .mapToDouble(SeepageHour::getLv3)
                    .max();


            SeepageDay seepageDay = new SeepageDay();
            seepageDay.setUpdateTime(start);
            seepageDay.setLv(maxLv.isPresent() ? maxLv.getAsDouble() : null);
            seepageDay.setLv2(maxLv2.isPresent() ? maxLv2.getAsDouble() : null);
            seepageDay.setLv3(maxLv3.isPresent() ? maxLv3.getAsDouble() : null);

            seepageDayService.saveOrUpdate(seepageDay,new LambdaUpdateWrapper<SeepageDay>()
                    .eq(SeepageDay::getUpdateTime,seepageDay.getUpdateTime()));


        }catch (Exception e){
            logger.error("渗流天级级报表更新发生异常",e);
        }
    }









    /**
     * #计算公式
     * @param inputPre
     * @param inputSuf
     * @return
     */
    public static Double calLv(Double inputPre,Double inputSuf){
        if (inputPre == null || inputSuf == null){
            return null;
        }
        if (inputSuf<inputPre){
            return null;
        }
        return (inputSuf-inputPre)*50;
    }


    /**
     * 量水堰计算公式
     * @param input
     * @return
     * @throws Exception
     */
    public static Double calLv2(Double input) throws Exception{
        if (input == null){
            return null;
        }
        if (input == 0){
            return 0.0;
        }
        double result = (1354 + 4/input +(140-200/Math.pow(0.03,0.5))*Math.pow((input/3-0.09),2))*Math.pow(input,2.5);
        if (result<0){
            return null;
        }
        return result;
    }


    /**
     * 汇水沟计算公式
     * @param input
     * @return
     * @throws Exception
     */
    public static Double calLv3(Double input) throws Exception{
        if (input == null){
            return null;
        }
        if (input == 0){
            return 0.0;
        }
        double result = 1000*(Math.pow(0.25,5.0/3)*0.1/0.03)*Math.pow(input,5.0/3)/Math.pow(((0.25+2*input)),2.0/3);
        if (result<0){
            return null;
        }
        return result;
    }







}
