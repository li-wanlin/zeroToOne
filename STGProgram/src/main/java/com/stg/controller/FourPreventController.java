package com.stg.controller;


import com.stg.service.impl.FourPreventServiceImpl;
import com.stg.service.impl.OverLimitServiceImpl;
import com.stg.service.impl.SafetyWarnServiceImpl;
import com.stg.utils.DateLocalUtils;
import com.stg.vo.fourPreventVo.*;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.matlabVo.MatlabPlanResponse;
import com.stg.vo.matlabVo.MatlabResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FourPreventController {

    @Resource
    FourPreventServiceImpl fourPreventService;

    @Resource
    OverLimitServiceImpl overLimitService;

    @Resource
    SafetyWarnServiceImpl safetyWarnService;



    private static final Logger logger = LoggerFactory.getLogger(FourPreventController.class);


    @GetMapping("/fourPrevent/Rain/selectRainList")
    public RainFourResponse selectRainList(){
        RainFourResponse response = new RainFourResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<RainRangeUnit> units = fourPreventService.selectRainList();
            if (units != null && units.size() > 0){
                response.setRainRangeUnits(units);
                response.setNowTime(DateLocalUtils.getGiveTimeToStr(LocalDateTime.now().withSecond(0).withNano(0), "yyyy-MM-dd HH:mm:ss"));
                meta.setStatus(200);
                meta.setMsg("获取最近四天雨量数据成功");
                return response;
            }
        }catch (Exception e){
            logger.error("获取最近四天雨量数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取最近四天雨量数据失败");
        return response;
    }



    @GetMapping("/fourPrevent/Rain/selectRainListSim")
    public RainFourResponse selectRainListSim(){
        RainFourResponse response = new RainFourResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            List<RainRangeUnit> units = fourPreventService.selectRainListSim();
            if (units != null && units.size() > 0){
                response.setRainRangeUnits(units);
                response.setNowTime(DateLocalUtils.getGiveTimeToStr(LocalDateTime.now().withSecond(0).withNano(0), "yyyy-MM-dd HH:mm:ss"));
                meta.setStatus(200);
                meta.setMsg("获取最近四天雨量数据成功");
                return response;
            }
        }catch (Exception e){
            logger.error("获取最近四天雨量数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取最近四天雨量数据失败");
        return response;
    }





    @GetMapping("/fourPrevent/Rain/calRain")
    public RainFourResponse calRain(){
        RainFourResponse response = new RainFourResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            RainData rainData = fourPreventService.calRain();
            if (rainData != null){
                response.setRainData(rainData);
                response.setNowTime(DateLocalUtils.getGiveTimeToStr(LocalDateTime.now().withSecond(0).withNano(0), "yyyy-MM-dd HH:mm:ss"));
                meta.setStatus(200);
                meta.setMsg("获取最近四天雨量计算数据成功");
                return response;
            }
        }catch (Exception e){
            logger.error("获取最近四天雨量计算数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取最近四天雨量计算数据失败");
        return response;
    }


    @GetMapping("/fourPrevent/Rain/calRainSim")
    public RainFourResponse calRainSim(){
        RainFourResponse response = new RainFourResponse();
        Meta meta = new Meta();
        response.setMeta(meta);

        try {
            RainData rainData = fourPreventService.calRainSim();
            if (rainData != null){
                response.setRainData(rainData);
                response.setNowTime(DateLocalUtils.getGiveTimeToStr(LocalDateTime.now().withSecond(0).withNano(0), "yyyy-MM-dd HH:mm:ss"));
                meta.setStatus(200);
                meta.setMsg("获取最近四天雨量计算数据成功");
                return response;
            }
        }catch (Exception e){
            logger.error("获取最近四天雨量计算数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取最近四天雨量计算数据失败");
        return response;
    }


    @GetMapping("/fourPrevent/OverLimit/selectLatestOver")
    public OverResponse selectLatestOver(){
        try {
            return overLimitService.selectLatestOver();
        }catch (Exception e){
            logger.error("获取onenet超限数据Controller层发生异常",e);
        }
        return null;
    }


    @GetMapping("/fourPrevent/OverLimit/pagedQueryOver")
    public OverResponse pagedQueryOver(@RequestParam(value = "pageNum") Integer pageNum,
                                           @RequestParam(value = "pageSize") Integer pageSize,
                                           @RequestParam(value = "year",required = false) String year){
        OverResponse response = new OverResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            OverResponse overResponse = overLimitService.pagedQueryOver(pageNum, pageSize,year);
            if (overResponse != null){
                return overResponse;
            }
        }catch (Exception e){
            logger.error("获取onenet超限分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取onenet超限分页数据发生异常");
        return response;

    }


    @GetMapping("/fourPrevent/SafetyWarn/selectLatestWarn")
    public WarnResponse selectLatestWarn(){
        try {
            return safetyWarnService.selectLatestOver();
        }catch (Exception e){
            logger.error("获取预警安全数据Controller层发生异常",e);
        }
        return null;
    }


    @GetMapping("/fourPrevent/SafetyWarn/pagedQueryWarn")
    public WarnResponse pagedQueryWarn(@RequestParam(value = "pageNum") Integer pageNum,
                                       @RequestParam(value = "pageSize") Integer pageSize,
                                       @RequestParam(value = "year",required = false) String year){
        WarnResponse response = new WarnResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            WarnResponse warnResponse = safetyWarnService.pagedQueryOver(pageNum, pageSize,year);
            if (warnResponse != null){
                return warnResponse;
            }
        }catch (Exception e){
            logger.error("获取预警安全分页数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取预警安全分页数据发生异常");
        return response;

    }


    @GetMapping("/fourPrevent/Matlab/selectForecastFlow")
    public MatlabResponse selectForecastFlow(){
        MatlabResponse response = new MatlabResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            MatlabResponse matlabResponse = fourPreventService.selectForecastFlow();
            if (matlabResponse != null){
                return matlabResponse;
            }
        }catch (Exception e){
            logger.error("获取入库流量预报数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取入库流量预报数据失败");
        return response;
    }


    @GetMapping("/fourPrevent/Matlab/selectFloodInfo")
    public MatlabResponse selectFloodInfo(){
        MatlabResponse response = new MatlabResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            MatlabResponse matlabResponse = fourPreventService.selectFloodInfo();
            if (matlabResponse != null){
                return matlabResponse;
            }
        }catch (Exception e){
            logger.error("获取洪水信息数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取洪水信息数据失败");
        return response;
    }


    @GetMapping("/fourPrevent/Matlab/selectFloodInfoSim")
    public MatlabResponse selectFloodInfoSim(){
        MatlabResponse response = new MatlabResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            MatlabResponse matlabResponse = fourPreventService.selectFloodInfoSim();
            if (matlabResponse != null){
                return matlabResponse;
            }
        }catch (Exception e){
            logger.error("获取洪水信息数据Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取洪水信息数据失败");
        return response;
    }





    @GetMapping("/fourPrevent/Matlab/selectPlan")
    public MatlabPlanResponse selectPlan(){
        MatlabPlanResponse response = new MatlabPlanResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            MatlabPlanResponse matlabPlanResponse = fourPreventService.selectPlan();
            if (matlabPlanResponse != null){
                return matlabPlanResponse;
            }
        }catch (Exception e){
            logger.error("获取预案Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取预案失败");
        return response;
    }


    @GetMapping("/fourPrevent/Matlab/selectPlanSim")
    public MatlabPlanResponse selectPlanSim(){
        MatlabPlanResponse response = new MatlabPlanResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {
            MatlabPlanResponse matlabPlanResponse = fourPreventService.selectPlanSim();
            if (matlabPlanResponse != null){
                return matlabPlanResponse;
            }
        }catch (Exception e){
            logger.error("获取预案Controller层发生异常",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取预案失败");
        return response;
    }



}
