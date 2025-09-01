package com.jnl.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jnl.entity.RainFallData;
import com.jnl.entity.RealTimeData;
import com.jnl.service.impl.*;
import com.jnl.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;




//@Component
public class RealTimeDataTask {

    @Resource
    OnenetDev1ServiceImpl onenetDev1Service;

    @Resource
    OnenetDev2ServiceImpl onenetDev2Service;

    @Resource
    OnenetDev3ServiceImpl onenetDev3Service;

    @Resource
    OnenetDev4ServiceImpl onenetDev4Service;

    @Resource
    OnenetDev5ServiceImpl onenetDev5Service;

    @Resource
    RealTimeDataServiceImpl realTimeDataService;


    @Resource
    RainFallDataServiceImpl rainFallDataService;


    private static final Logger logger = LoggerFactory.getLogger(RealTimeDataTask.class);



    @Scheduled(fixedRate = 1000 * 10)
    @Async("asyncExecutor")
    public void Weather(){
        try {

            RealTimeData realTimeData = new RealTimeData();
            RainUnit unitOne = onenetDev1Service.selectRealTimeRain();
            RainUnit unitTwo = onenetDev2Service.selectRealTimeRain();
            RainUnit unitThree = onenetDev3Service.selectRealTimeRain();
            RainUnit unitFour = onenetDev4Service.selectRealTimeRain();
            RainUnit unitFive = onenetDev5Service.selectRealTimeRain();

            if (unitOne==null&&unitTwo==null&&unitThree==null&&unitFour==null&&unitFive==null){
                return;
            }

            realTimeData.setId(1);
            if (unitOne != null){
                realTimeData.setRainfall1(unitOne.getValue());
                realTimeData.setInputTime(unitOne.getUpdateTime());
            }

            if (unitTwo != null){
                realTimeData.setRainfall2(unitTwo.getValue());
                realTimeData.setInputTime(unitTwo.getUpdateTime());
            }

            if (unitThree != null){
                realTimeData.setRainfall3(unitThree.getValue());
                realTimeData.setInputTime(unitThree.getUpdateTime());
            }

            if (unitFour != null){
                realTimeData.setRainfall4(unitFour.getValue());
                realTimeData.setInputTime(unitFour.getUpdateTime());
            }

            if (unitFive != null){
                realTimeData.setRainfall5(unitFive.getValue());
                realTimeData.setInputTime(unitFive.getUpdateTime());
            }


            if (realTimeData.getInputTime() != null){
                realTimeDataService.saveOrUpdate(realTimeData);
            }


            RainFallData rainFallData = new RainFallData();
            rainFallData.setInputTime(realTimeData.getInputTime());
            rainFallData.setRainfall1(realTimeData.getRainfall1());
            rainFallData.setRainfall2(realTimeData.getRainfall2());
            rainFallData.setRainfall3(realTimeData.getRainfall3());
            rainFallData.setRainfall4(realTimeData.getRainfall4());
            rainFallData.setRainfall5(realTimeData.getRainfall5());

            rainFallDataService.saveOrUpdate(rainFallData,new LambdaUpdateWrapper<RainFallData>()
                    .eq(RainFallData::getInputTime,rainFallData.getInputTime()));



            logger.info("实时雨量定时任务执行成功");

        }catch (Exception e){
            logger.error("实时雨量定时任务执行发生异常",e);
        }



    }





}
