package com.jnl.controller;


import com.jnl.service.impl.OnenetServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.onenetVo.DevUnit;
import com.jnl.vo.onenetVo.OnenetResponse;
import com.jnl.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OnenetController {


    @Resource
    OnenetServiceImpl onenetService;


    private static final Logger logger = LoggerFactory.getLogger(OnenetController.class);


    @GetMapping("/homePage/Onenet/selectRealTimeRain")
    public OnenetResponse selectRealTimeRain(){
        OnenetResponse response = new OnenetResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<RainUnit> rainUnits = onenetService.selectRealTimeRain();
            List<RainUnit> rainUnitList = rainUnits.stream()
                    .filter(rainUnit -> rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainUnitList.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取实时雨量数据成功");
                response.setRainUnits(rainUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取实时雨量数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取实时雨量数据失败");
        return response;
    }

    @GetMapping("/homePage/Onenet/generalDay")
    public OnenetResponse generalDay(@RequestParam("dateStr")String dateStr){
        OnenetResponse response = new OnenetResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<RainUnit> rainUnits = onenetService.generalDay(dateStr);
            List<RainUnit> rainUnitList = rainUnits.stream()
                    .filter(rainUnit -> rainUnit.getValue() != null)
                    .collect(Collectors.toList());
            if (rainUnitList.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取历史雨量数据成功");
                response.setRainUnits(rainUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取历史雨量数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取历史雨量数据失败");
        return response;
    }


    @GetMapping("/homePage/Onenet/selectLatest")
    public OnenetResponse selectLatest(){
        OnenetResponse response = new OnenetResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<DevUnit> devUnits = onenetService.selectLatest();
            if (devUnits != null){
                meta.setStatus(200);
                meta.setMsg("获取onenet实时数据成功");
                response.setDevUnits(devUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取onenet实时数据发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取onenet实时数据失败");
        return response;
    }

    @GetMapping("/fourTotal/Onenet/dataByGivenHour")
    public OnenetResponse dataByGivenHour(@RequestParam Integer agoHourNum){
        OnenetResponse response = new OnenetResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            OnenetResponse onenetResponse = onenetService.dataByGivenHour(agoHourNum);
            if (onenetResponse != null){
                return onenetResponse;
            }
        }catch (Exception e){
            logger.error("获取onenet历史数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取onenet历史数据失败");
        return response;
    }



}
