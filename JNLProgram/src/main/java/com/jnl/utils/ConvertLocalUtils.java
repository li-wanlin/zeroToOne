package com.jnl.utils;

import com.jnl.entity.XSJData;
import com.jnl.vo.xsjVo.XSJInsertVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertLocalUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConvertLocalUtils.class);


    public static XSJData toXSJData(XSJInsertVo vo){
        XSJData xsjData = new XSJData();
        try{
            xsjData.setTime(vo.getTime());
            xsjData.setSec(vo.getSec());
            xsjData.setStage(vo.getStage());
            xsjData.setPowerSum(vo.getPowerSum());
            xsjData.setPowerOne(vo.getPowerOne());
            xsjData.setPowerTwo(vo.getPowerTwo());
            xsjData.setPowerThree(vo.getPowerThree());
        }catch (Exception e){
            logger.error("新世纪数据转换出错",e);
        }
        return xsjData;
    }

}
