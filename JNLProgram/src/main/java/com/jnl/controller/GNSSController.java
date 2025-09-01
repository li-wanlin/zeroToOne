package com.jnl.controller;


import com.jnl.service.impl.GNSSDataServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.southVo.GNSSResponse;
import com.jnl.vo.southVo.MonitorUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class GNSSController {

    @Resource
    GNSSDataServiceImpl gnssDataService;

    private static final Logger logger = LoggerFactory.getLogger(GNSSController.class);



    @GetMapping("/homePage/GNSS/selectLatestGNSS")
    public GNSSResponse selectLatestGNSS(){
        GNSSResponse response = new GNSSResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            List<MonitorUnit> monitorUnits = gnssDataService.selectLatestGNSS();
            if (monitorUnits != null && monitorUnits.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取GNSS实时数据成功");
                response.setMonitorUnits(monitorUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取GNSS实时数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取GNSS实时数据失败");
        return response;
    }


    @GetMapping("/homePage/GNSS/selectFortyEight")
    public GNSSResponse selectFortyEight(){
        GNSSResponse response = new GNSSResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            List<MonitorUnit> monitorUnits = gnssDataService.selectFortyEight();
            if (monitorUnits != null && monitorUnits.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取GNSS48小时数据成功");
                response.setMonitorUnits(monitorUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取GNSS48小时数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取GNSS48小时数据失败");
        return response;
    }


    @GetMapping("/fourTotal/GNSS/dataByGivenHourGNSS")
    public GNSSResponse dataByGivenHourGNSS(@RequestParam Integer agoHourNum){
        GNSSResponse response = new GNSSResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            List<MonitorUnit> monitorUnits = gnssDataService.dataByGivenHourGNSS(agoHourNum);
            if (monitorUnits != null && monitorUnits.size() > 0){
                meta.setStatus(200);
                meta.setMsg("获取GNSS历史数据成功");
                response.setMonitorUnits(monitorUnits);
                return response;
            }
        }catch (Exception e){
            logger.error("获取GNSS历史数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取GNSS历史数据失败");
        return response;
    }



}
