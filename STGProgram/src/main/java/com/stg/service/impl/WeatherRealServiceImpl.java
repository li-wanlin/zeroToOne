package com.stg.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stg.entity.WeatherReal;
import com.stg.mapper.WeatherRealMapper;
import com.stg.service.WeatherRealService;
import com.stg.utils.HttpLocalUtils;
import com.stg.vo.WeatherVo.RealBody;
import com.stg.vo.WeatherVo.XZBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherRealServiceImpl extends ServiceImpl<WeatherRealMapper, WeatherReal> implements WeatherRealService {

    @Resource
    WeatherRealMapper weatherRealMapper;

    private static final Logger logger = LoggerFactory.getLogger(WeatherRealServiceImpl.class);



    @Override
    public WeatherReal selectRealTime() {
        try {

            QueryWrapper<WeatherReal> queryWrapper = new QueryWrapper<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusHours(1);
            queryWrapper.between("date_time",start,now);
            queryWrapper.orderByDesc("date_time");
            List<WeatherReal> weatherReals = weatherRealMapper.selectList(queryWrapper);
            if (weatherReals != null || weatherReals.size() > 0){
                return weatherReals.get(0);
            }


            WeatherReal real = this.WeatherRealCall();
            if (real != null){
                return real;
            }


        }catch (Exception e){
            logger.error("获取彩云实时天气接口发生异常",e);
        }
        return null;
    }


    @Override
    public WeatherReal WeatherRealCall() {
        try {
            //洛阳市栾川县庙子镇龙王幢村：经度：111.721420，纬度：33.844870
/*            String url = "https://api.caiyunapp.com/v2.6/kVzkspcZCGZBAtXG/111.721420,33.844870/realtime";
            String response = HttpLocalUtils.sendGetRequest(url);
            RealBody realBody = JSON.parseObject(response, RealBody.class);
            if (realBody == null || realBody.getStatus() == null || !"ok".equals(realBody.getStatus())
                    || realBody.getResult() == null
                    || realBody.getResult().getRealTime() == null || realBody.getResult().getRealTime().getTemperature() == null
                    || realBody.getResult().getRealTime().getSkycon() == null){
                return null;
            }

            Map<String, String> map = new HashMap<>();
            map.put("CLEAR_DAY","晴");
            map.put("CLEAR_NIGHT","晴（夜间）");
            map.put("PARTLY_CLOUDY_DAY","多云（白天）");
            map.put("PARTLY_CLOUDY_NIGHT","多云（夜间）");
            map.put("CLOUDY","阴");
            map.put("LIGHT_HAZE","轻度雾霾");
            map.put("MODERATE_HAZE","中度雾霾");
            map.put("HEAVY_HAZE","重度雾霾");
            map.put("LIGHT_RAIN","小雨");
            map.put("MODERATE_RAIN","中雨");
            map.put("HEAVY_RAIN","大雨");
            map.put("STORM_RAIN","暴雨");
            map.put("FOG","雾");
            map.put("LIGHT_SNOW","小雪");
            map.put("MODERATE_SNOW","中雪");
            map.put("HEAVY_SNOW","大雪");
            map.put("STORM_SNOW","暴雪");
            map.put("DUST","浮尘");
            map.put("SAND","沙尘");
            map.put("WIND","大风");


            WeatherReal weatherReal = new WeatherReal();
            weatherReal.setId(1);
            weatherReal.setDateTime(new Date());
            weatherReal.setTemperature(realBody.getResult().getRealTime().getTemperature());
            weatherReal.setSkycon(map.get(realBody.getResult().getRealTime().getSkycon()));
            saveOrUpdate(weatherReal);
            return weatherReal;*/


            //心知天气接口
            String url = "https://api.seniverse.com/v3/weather/now.json?key=St5xNndPc6p18OUKe&location=33.844870:111.721420&language=zh-Hans&unit=c";
            String response = HttpLocalUtils.sendGetRequest(url);
            XZBody xzBody = JSON.parseObject(response, XZBody.class);
            if (xzBody == null || xzBody.getResults() == null
                    || xzBody.getResults().getNow() == null || xzBody.getResults().getNow().getTemperature() == null
                    || xzBody.getResults().getNow().getText() == null){
                return null;
            }


            WeatherReal weatherReal = new WeatherReal();
            weatherReal.setId(1);
            weatherReal.setDateTime(new Date());
            weatherReal.setTemperature(Double.parseDouble(xzBody.getResults().getNow().getTemperature()));
            weatherReal.setSkycon(xzBody.getResults().getNow().getText());
            saveOrUpdate(weatherReal);
            return weatherReal;
        }catch (Exception e){
            logger.error("定时获取天气预报实时数据失败",e);
        }
        return null;
    }
}
