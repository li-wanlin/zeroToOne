package com.stg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PubUtils {

    private static final Logger logger = LoggerFactory.getLogger(PubUtils.class);


    /**
     * 将Float保留给定的小数位
     * @param value
     * @param position
     * @return
     */
    public static Float keepGiveDecimal(Float value, Integer position){
        try {
            if (value == null || position == null || position <= 0){
                return null;
            }

            BigDecimal decimal = new BigDecimal(Float.toString(value));
            decimal = decimal.setScale(1, RoundingMode.HALF_UP);

            return decimal.floatValue();
        }catch (Exception e){
            logger.error("再将Float保留小数位时发生异常",e);
        }
        return null;
    }


}
