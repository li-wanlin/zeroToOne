package com.jnl.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.WeatherForecast;
import com.jnl.mapper.WeatherMapper;
import com.jnl.service.WeatherService;
import com.jnl.utils.DateLocalUtils;
import com.jnl.utils.HttpLocalUtils;
import com.jnl.vo.WeatherVo.ForecastBody;
import com.jnl.vo.WeatherVo.PreUnit;
import com.jnl.vo.WeatherVo.TemUnit;
import com.jnl.vo.fourPreventVo.RainRangeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class WeatherServiceImpl extends ServiceImpl<WeatherMapper, WeatherForecast> implements WeatherService {

    @Resource
    WeatherMapper weatherMapper;

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);



    @Override
    public WeatherForecast selectLatest() {
        try {
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime start = nowHour.minusHours(1);
            QueryWrapper<WeatherForecast> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("date_time",start,nowHour);
            queryWrapper.orderByDesc("date_time");

            List<WeatherForecast> forecasts = weatherMapper.selectList(queryWrapper);
            if (forecasts != null && forecasts.size() > 0){
                return forecasts.get(0);
            }
        }catch (Exception e){
            logger.error("获取天气预报实时数据出错",e);
        }

        return null;
    }


    @Override
    public void weatherCall() {
        try {

            //logger.info("kaishizhixing");
            LocalDateTime now = LocalDateTime.now();
            String timeToStr = DateLocalUtils.parseTimeToStr(now);

            //logger.info("此日志用来打印调试获取天气预报的时间:{}",timeToStr);

            //洛阳市栾川县庙子镇龙王幢村：经度：111.721420，纬度：33.844870
            String url = "https://api.caiyunapp.com/v2.6/kVzkspcZCGZBAtXG/111.721420,33.844870/hourly?hourlysteps=72";
            String response = HttpLocalUtils.sendGetRequest(url);
            ForecastBody forecastBody = JSON.parseObject(response, ForecastBody.class);
            if (forecastBody == null || forecastBody.getStatus() == null || !"ok".equals(forecastBody.getStatus())
                    || forecastBody.getResult() == null
                    || forecastBody.getResult().getHourly() == null || forecastBody.getResult().getHourly().getPrecipitation() == null
                    || forecastBody.getResult().getHourly().getTemperature() == null
                    || forecastBody.getResult().getHourly().getPrecipitation().size() == 0
                    || forecastBody.getResult().getHourly().getTemperature().size() == 0){
                return;
            }

            List<PreUnit> precipitation = forecastBody.getResult().getHourly().getPrecipitation();
            List<TemUnit> temperature = forecastBody.getResult().getHourly().getTemperature();


            List<WeatherForecast> forecasts = new ArrayList<>();
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            for (int i = 0; i < precipitation.size(); i++) {
                PreUnit preUnit = precipitation.get(i);
                TemUnit temUnit = temperature.get(i);
                WeatherForecast forecast = new WeatherForecast();

                LocalDateTime inputTime = nowHour.plusHours(i);

                //将降水量和温度的值存入
                forecast.setPrecipitation(preUnit.getValue());
                forecast.setTemperature(temUnit.getValue());
                forecast.setDateTime(DateLocalUtils.parseTimeToDate(inputTime));

                forecasts.add(forecast);
            }


            Boolean insertOrUpdate = weatherMapper.batchInsertOrUpdate(forecasts);

            if (insertOrUpdate){
                logger.info("每小时天气预报定时任务更新成功");
            }else {
                logger.info("每小时天气预报定时任务更新失败");
            }

        }catch (Exception e){
            logger.error("每小时天气预报定时任务更新发生异常",e);
        }
    }


    @Override
    public List<RainRangeUnit> forecastThree() {
        try {

            List<WeatherForecast> forecasts;
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime afterThree = nowHour.plusHours(71);

            forecasts = this.selectFore(nowHour, afterThree);

            List<WeatherForecast> judge = forecasts.stream().filter(weatherForecast -> afterThree.equals(DateLocalUtils.parseDateToTime(weatherForecast.getDateTime())))
                    .collect(Collectors.toList());

            if (judge.size()>0){
                return this.listToRange(forecasts);
            }else {
                this.weatherCall();
                forecasts = this.selectFore(nowHour,afterThree);
                return this.listToRange(forecasts);
            }

        }catch (Exception e){
            logger.error("获取天气预测数据出错",e);
        }
        return null;
    }


    public Map<String,Double> calAverageCall(){
        try {
            Map<String, Double> map = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nightNow = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime nightAfter = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

            QueryWrapper<WeatherForecast> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("date_time",nightNow,nightAfter);

            List<WeatherForecast> forecasts = weatherMapper.selectList(queryWrapper);
            if (forecasts == null || forecasts.size() == 0){
                return null;
            }
            double average = forecasts.stream().mapToDouble(WeatherForecast::getPrecipitation).average().orElse(0.0);
            map.put("nowDayRain",average);

            return map;
        }catch (Exception e){
            logger.error("更新天气预报日降雨平均值发生异常");
        }
        return null;
    }



    public List<WeatherForecast> selectFore(LocalDateTime nowHour,LocalDateTime endHour){
        try {
            QueryWrapper<WeatherForecast> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("date_time",nowHour,endHour);
            queryWrapper.orderByAsc("date_time");

            List<WeatherForecast> forecasts = weatherMapper.selectList(queryWrapper);
            if (forecasts == null || forecasts.size() == 0){
                return null;
            }
            return forecasts;

        }catch (Exception e){
            logger.error("获取天气预测数据出错",e);
        }
        return null;
    }



    public List<RainRangeUnit> listToRange(List<WeatherForecast> rainFallDatas){
        try {
            if (rainFallDatas == null || rainFallDatas.size() == 0){
                return null;
            }

            List<RainRangeUnit> units = new ArrayList<>();
            for (WeatherForecast ra:rainFallDatas) {

                RainRangeUnit unit = new RainRangeUnit();
                unit.setRainTime(DateLocalUtils.parseDateToStr(ra.getDateTime()));
                unit.setValue(ra.getPrecipitation()== null?null:(double)ra.getPrecipitation());

                units.add(unit);
            }

            return units;
        }catch (Exception e){
            logger.error("转换天气预测数据出错",e);
        }
        return null;
    }


    public Double calDiff(List<WeatherForecast> forecasts){
        try {

            List<WeatherForecast> fallData = forecasts.stream()
                    .filter(weatherForecast -> weatherForecast.getPrecipitation() != null)
                    .collect(Collectors.toList());

            if (fallData.size() == 0){
                return null;
            }


            forecasts.stream()
                    .filter(weatherForecast -> weatherForecast.getPrecipitation() == null)
                    .forEach(weatherForecast -> weatherForecast.setPrecipitation(0.0));


            double diffSum = IntStream.range(0, forecasts.size() - 1)
                    .mapToDouble(i -> {
                        WeatherForecast rainPre = forecasts.get(i);
                        WeatherForecast rainSuf = forecasts.get(i + 1);
                        return (rainSuf.getPrecipitation() - rainPre.getPrecipitation()) >= 0
                                ? (rainSuf.getPrecipitation() - rainPre.getPrecipitation()) : rainSuf.getPrecipitation();
                    })
                    .sum();
            return diffSum;

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }

}
