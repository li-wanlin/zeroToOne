/*
package com.jnl.task;

import com.jnl.manage.MatlabEngineManager;
import com.jnl.sevice.RealTimeFlowService;

import com.jnl.sevice.impl.RealTimeFlowServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Component
public class MatlabTask {

    @Resource
    MatlabEngineManager matlabEngineManager;


    @Resource
    MatlabServiceImpl matlabService;


    @Resource
    RealTimeFlowServiceImpl realTimeFlowService;


    private static final Logger logger = LoggerFactory.getLogger(MatlabTask.class);


    */
/**
     * 每小时释放matlab无用资源
     *//*

    @Scheduled(fixedRate = 1000 * 60 * 60)
    @Async("asyncExecutor")
    public void releaseMatlab(){
        //logger.info("开始释放");
        matlabEngineManager.releaseInactiveEngines();
    }




    */
/**
     * 计算实时发电流量、实时泄洪流量
     *//*


    @Scheduled(cron = "0 0/5 * * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 5)
    @Async("asyncExecutor")
    public void skw2Cal(){
        try {
            matlabService.calculateSkw2();
        }catch (Exception e){
            logger.error("",e);
        }
    }






    */
/**
     * 入库流量预报
     *//*


    @Scheduled(cron = "0 0 0/1 * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 5)
    @Async("asyncExecutor")
    public void rycCal(){
        try {
            matlabService.calculateRyc();
        }catch (Exception e){
            logger.error("",e);
        }


    }


    */
/**
     * 小时平均来流量
     *//*


    @Scheduled(cron = "0 0 0/1 * * ?")
    //@Scheduled(fixedRate = 1000 * 60 * 10)
    @Async("asyncExecutor")
    public void skw1Cal(){
        try {
            matlabService.calculateSkw1();
        }catch (Exception e){
            logger.error("",e);
        }
    }





    */
/**
     * 洪水演进计算
     *//*


    @Scheduled(cron = "0 0/5 * * * ?")
    @Async("asyncExecutor")
    public void hsyjCal(){
        try {
            matlabService.calculateHsyj();
        }catch (Exception e){
            logger.error("洪水演进计算异常",e);
        }
    }



}
*/
