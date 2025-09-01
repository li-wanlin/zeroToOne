package com.jnl.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jnl.entity.FloodControl;
import com.jnl.mapper.FloodControlMapper;
import com.jnl.service.FloodControlService;
import com.jnl.utils.DateLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FloodControlServiceImpl extends ServiceImpl<FloodControlMapper, FloodControl> implements FloodControlService {

    @Resource
    FloodControlMapper floodControlMapper;


    private static final Logger logger = LoggerFactory.getLogger(FloodControlServiceImpl.class);


    @Override
    public FloodControl selectFloodControl() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(15);
            QueryWrapper<FloodControl> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("time",start,now);
            queryWrapper.orderByDesc("time");

            List<FloodControl> floodControls = floodControlMapper.selectList(queryWrapper);
            if (floodControls != null && floodControls.size() > 0){
                return floodControls.get(0);
            }
        }catch (Exception e){
            logger.error("");
        }
        return null;
    }



    @Override
    public Map<String, String> selectPlan() {
        try {
            QueryWrapper<FloodControl> queryWrapper = new QueryWrapper<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMinutes(15);
            queryWrapper.between("time",start,now);
            queryWrapper.orderByDesc("time");
            List<FloodControl> floodControls = floodControlMapper.selectList(queryWrapper);
            if (floodControls  == null || floodControls.size() == 0){
                return null;
            }

            double[] stages = JSON.parseObject(floodControls.get(0).getStage(),double[].class);
            double[] flows = JSON.parseObject(floodControls.get(0).getFlow(),double[].class);


            Map<String, String> map = new HashMap<>();
            String display = null;  //需显示的文字

            double maxStage = 0;    //最大水位
            String maxTime = null;  //最大水位出现时间
            Integer startIndex = -1;    //首次大于给定值位置
            String startTime = null;    //首次大于给定值时间
            Integer endIndex = -1;      //最后一次大于给定值位置
            String endTime = null;      //最后一次大于给定值时间
            int displayFlow;
            int displayStage;


            double maxFlow = flows[0];
            Integer maxIndex = 0;

            for (int i = 0; i < flows.length; i++) {
                if (maxFlow<flows[i]){
                    maxFlow = flows[i];
                    maxIndex = i;
                }
            }

            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

            maxStage = stages[maxIndex];
            maxTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(maxIndex));

            if (maxFlow > 2100){

                for (int i = 0; i < flows.length; i++) {
                    if (flows[i] > 1724){
                        startIndex = i;
                        break;
                    }
                }

                startTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(startIndex));

                for (int i = flows.length-1; i >=0 ; i--) {
                    if (flows[i] > 1724){
                        endIndex = i;
                        break;
                    }
                }

                endTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(endIndex));

                displayFlow = (int)maxFlow;
                displayStage = (int)maxStage;


                display = "未来72小时内，水库预计最大泄量"+displayFlow+"m3//s，最高水位"+displayStage+"m，出现时间为"+maxTime
                        +"，应启动Ⅰ级红色应急响应；预计下泄流量大于1724m3//s的时段为"+startTime+"至"+endTime+"，共计"+(endIndex-startIndex)+"小时";

                map.put("display",display);
                map.put("planLv","1");

            }else if (maxFlow > 1500){

                for (int i = 0; i < flows.length; i++) {
                    if (flows[i] > 1280){
                        startIndex = i;
                        break;
                    }
                }

                startTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(startIndex));

                for (int i = flows.length-1; i >=0 ; i--) {
                    if (flows[i] > 1280){
                        endIndex = i;
                        break;
                    }
                }

                endTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(endIndex));

                displayFlow = (int)maxFlow;
                displayStage = (int)maxStage;


                display = "未来72小时内，水库预计最大泄量"+displayFlow+"m3//s，最高水位"+displayStage+"m，出现时间为"+maxTime
                        +"，应启动Ⅱ级橙色应急响应；预计下泄流量大于1280m3//s的时段为"+startTime+"至"+endTime+"，共计"+(endIndex-startIndex)+"小时";

                map.put("display",display);
                map.put("planLv","2");
            }else if (maxFlow > 1000){
                for (int i = 0; i < flows.length; i++) {
                    if (flows[i] > 850){
                        startIndex = i;
                        break;
                    }
                }

                startTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(startIndex));

                for (int i = flows.length-1; i >=0 ; i--) {
                    if (flows[i] > 850){
                        endIndex = i;
                        break;
                    }
                }

                endTime = DateLocalUtils.parseTimeToStr(nowHour.plusHours(endIndex));


                displayFlow = (int)maxFlow;
                displayStage = (int)maxStage;

                display = "未来72小时内，水库预计最大泄量"+displayFlow+"m3//s，最高水位"+displayStage+"m，出现时间为"+maxTime
                        +"，应启动Ⅲ级黄色应急响应；预计下泄流量大于850m3//s的时段为"+startTime+"至"+endTime+"，共计"+(endIndex-startIndex)+"小时";

                map.put("display",display);
                map.put("planLv","3");
            }else {
                display = "未来72小时内，无需启动应急响应，请关注天气变化";

                map.put("display",display);
                map.put("planLv","0");
            }


            return map;


        }catch (Exception e){
            logger.error("",e);
        }

        return null;
    }

    @Override
    public Map<String, String> selectPlanSim() {
        try {

            //未来72小时内，水库预计最大泄量1946.93//s，最高水位679.97m，出现时间为(25日14时)，应启动Ⅱ级橙色应急响应；
            // 预计下泄流量大于1280m3//s的时段为(25日10h)至(25日16h)，共计6小时；
            Map<String, String> map = new HashMap<>();
            String planLv = "2";
            LocalDateTime nowHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            String startHour = DateLocalUtils.parseTimeToStr(nowHour.plusHours(26));
            String maxHour = DateLocalUtils.parseTimeToStr(nowHour.plusHours(28));
            String endHour = DateLocalUtils.parseTimeToStr(nowHour.plusHours(32));

            String display = "未来72小时内，水库预计最大泄量1946.93m³/s，最高水位679.97m，出现时间为"+maxHour
                    +"应启动Ⅱ级橙色应急响应；预计下泄流量大于1280m³/s的时段为"+startHour+"至"+endHour+"，共计6小时";
            map.put("display",display);
            map.put("planLv",planLv);
            return map;

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


}
