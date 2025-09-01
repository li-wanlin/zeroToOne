package com.jnl.service.impl;

import com.jnl.controller.FourPreventController;
import com.jnl.entity.AverageFlow;
import com.jnl.entity.RealTimeFlow;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.matlabVo.MatlabWaterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FourTotalServiceImpl {



    @Resource
    AverageFlowServiceImpl averageFlowService;


    @Resource
    RealTimeFlowServiceImpl realTimeFlowService;


    private static final Logger logger = LoggerFactory.getLogger(FourTotalServiceImpl.class);



    public MatlabWaterResponse selectMatlabWater(){
        MatlabWaterResponse response = new MatlabWaterResponse();
        Meta meta = new Meta();
        response.setMeta(meta);
        try {

            AverageFlow averageFlow = averageFlowService.selectAverageFlow();
            if (averageFlow != null){
                response.setAverageFlow(averageFlow.getFlow());
            }

            RealTimeFlow realTimeFlow = realTimeFlowService.selectRealTimeFlow();

            if (realTimeFlow != null){
                response.setPowerFlow(realTimeFlow.getPower());
                response.setFloodFlow(realTimeFlow.getFlood());
            }

            if (averageFlow == null && realTimeFlow == null){
                meta.setStatus(400);
                meta.setMsg("获取matlab实时水情失败");
            }else {
                meta.setStatus(200);
                meta.setMsg("获取matlab实时水情成功");
            }

            return response;

        }catch (Exception e){
            logger.error("获取matlab实时水情发生异常",e);
        }
        return null;
    }





}
