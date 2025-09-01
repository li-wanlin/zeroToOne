package com.jnl.controller;

import com.jnl.entity.XSJData;
import com.jnl.service.impl.XSJDataServiceImpl;
import com.jnl.utils.DateLocalUtils;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.xsjVo.XSJResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class XSJDataController {

    @Resource
    XSJDataServiceImpl xsjDataService;


    private static final Logger logger = LoggerFactory.getLogger(XSJDataController.class);


    @GetMapping("/insertByGive")
    public String insertByGive(){
        Boolean insert = xsjDataService.insertByGive();
        if (insert){
            return "insert success";
        }
        return "insert failed";
    }


    @GetMapping("/deleteByGiveId")
    public String deleteByGiveId(){
        Boolean delete = xsjDataService.deleteByGiveId(2);
        if (delete){
            return "delete success";
        }
        return "delete failed";
    }

    @GetMapping("/updateByGiveId")
    public String updateByGiveId(){
        Boolean update = xsjDataService.updateByGiveId(3);
        if (update){
            return "update success";
        }
        return "update failed";
    }

    @GetMapping("/selectDataById")
    public XSJData selectDataById(){
        XSJData xsjData = xsjDataService.selectDataById(1);
        if (xsjData!= null && xsjData.getId() != null){
            return xsjData;
        }
        return null;
    }

    @GetMapping("/homePage/selectLatestData")
    public XSJResponse selectLatestData(){
        XSJResponse xsjResponse = new XSJResponse();
        Meta meta = new Meta();
        xsjResponse.setMeta(meta);
        try {
            XSJData xsjData = xsjDataService.selectLatestData();
            if (xsjData != null && xsjData.getId() != null){
                meta.setStatus(200);
                meta.setMsg("获取新世纪数据成功");
                xsjResponse.setStage(xsjData.getStage());
                xsjResponse.setPowerSum(xsjData.getPowerSum().intValue());
                xsjResponse.setPowerOne(xsjData.getPowerOne()!=null?xsjData.getPowerOne().intValue():0);
                xsjResponse.setPowerTwo(xsjData.getPowerTwo().intValue());
                xsjResponse.setPowerThree(xsjData.getPowerThree().intValue());
                xsjResponse.setAccrue(xsjData.getAccrue());
                xsjResponse.setCapacity(xsjData.getCapacity());
                xsjResponse.setTime(DateLocalUtils.parseDateToStr(xsjData.getTime()));
                return xsjResponse;
            }
        }catch (Exception e){
            logger.error("获取新世界数据Controller层出错",e);
        }
        meta.setStatus(400);
        meta.setMsg("获取新世纪数据失败");
        return xsjResponse;
    }


}
