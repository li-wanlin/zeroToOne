package com.stg.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.stg.entity.RainFallData;
import com.stg.entity.RealTimeData;
import com.stg.service.impl.*;
import com.stg.vo.onenetVo.RainUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Component
public class RealTimeDataTask {

    @Resource
    OnenetDev21ServiceImpl onenetDev21Service;

    @Resource
    OnenetDev22ServiceImpl onenetDev22Service;

    @Resource
    OnenetDev1ServiceImpl onenetDev1Service;



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
            RainUnit unitOne = onenetDev21Service.selectRealTimeRain();
            RainUnit unitTwo = onenetDev22Service.selectRealTimeRain();

            RainUnit unitThree = onenetDev1Service.selectRealTimeRain();


            if (unitOne==null&&unitTwo==null){
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

            //备注：雨量3数据存储金牛岭设备1，只显示，不参与计算
            if (unitThree != null){
                realTimeData.setRainfall3(unitThree.getValue());
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
