package com.jnl.controller;

import com.jnl.service.impl.SeepageInfoServiceImpl;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.seepageVo.SeepageResponse;
import com.jnl.vo.seepageVo.SeepageUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SeepageController {

    @Resource
    SeepageInfoServiceImpl seepageInfoService;

    private static final Logger logger = LoggerFactory.getLogger(SeepageController.class);



    @GetMapping("/fourTotal/Seepage/getSixHour")
    public SeepageResponse getSixHour(@RequestParam String type){
        SeepageResponse response = new SeepageResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<SeepageUnit> sixHour = seepageInfoService.getSixHour(type);
            if (sixHour != null){
                response.setUnits(sixHour);
                meta.setStatus(200);
                meta.setMsg("获取渗流量6小时数据成功");
                return response;
            }

        }catch (Exception e){
            logger.error("",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取渗流量6小时数据失败");
        return response;
    }


    @GetMapping("/fourTotal/Seepage/getLatest")
    public SeepageResponse getLatest(){
        SeepageResponse response = new SeepageResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<SeepageUnit> latest = seepageInfoService.getLatest();
            if (latest != null){
                response.setUnits(latest);
                meta.setStatus(200);
                meta.setMsg("获取渗流量实时数据成功");
                return response;
            }

        }catch (Exception e){
            logger.error("",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取渗流量实时数据失败");
        return response;
    }



}
