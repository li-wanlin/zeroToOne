package com.jnl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateLocalUtils {

    //以下操作皆为线程安全
    private static final String formatOne = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter formatterMill = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Logger logger = LoggerFactory.getLogger(DateLocalUtils.class);




    /**
     * 获取指定格式的当前时间
     * @param formatStr
     * @return
     */
    public static String getGiveFormatNow(String formatStr){
        try{
            if (formatStr == null || formatStr.length() < 10){
                return null;
            }
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter patternStr = DateTimeFormatter.ofPattern(formatStr);
            return now.format(patternStr);
        }catch (Exception e){
            logger.error("获取当前日期指定格式出错",e);
        }
        return null;
    }

    /**
     * 获取当前时间的时间戳
     * @return
     */
    public static Long getNowLong(){
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 将时间戳转化为Date类型对象
     * @param timeStamp
     * @return
     */
    public static Date parseLongToDate(Long timeStamp){
        try{
            if (timeStamp == null || timeStamp == 0){
                return null;
            }
            Instant instant = Instant.ofEpochMilli(timeStamp);
            return Date.from(instant);
        }catch (Exception e){
            logger.error("在将时间戳转化为Date格式时出错",e);
        }
        return null;
    }


    /**
     * 将时间戳转化为String类型对象,格式yyyy-MM-dd HH:mm:ss
     * @param timeStamp
     * @return
     */
    public static String parseLongToStr(Long timeStamp){
        try{
            if (timeStamp == null || timeStamp == 0){
                return null;
            }
            Instant instant = Instant.ofEpochMilli(timeStamp);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return localDateTime.format(formatter);
        }catch (Exception e){
            logger.error("在将时间戳转化为String格式时出错",e);
        }
        return null;
    }


    /**
     * 将时间戳转化为String类型对象，格式yyyy-MM-dd HH:mm:ss.SSS
     * @param timeStamp
     * @return
     */
    public static String parseLongToMilli(Long timeStamp){
        try{
            if (timeStamp == null || timeStamp == 0){
                return null;
            }
            Instant instant = Instant.ofEpochMilli(timeStamp);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            return localDateTime.format(formatterMill);
        }catch (Exception e){
            logger.error("在将时间戳转化为String格式时出错",e);
        }
        return null;
    }



    /**
     * 将String类型对象转化为Date类型对象(yyyy-MM-dd HH:mm:ss)
     * @param timeStr
     * @return
     */
    public static Date parseStrToDate(String timeStr){
        try {
            if (timeStr == null || timeStr.length() < 19){
                return null;
            }
            timeStr = timeStr.substring(0,19);
            LocalDateTime localDateFormat = LocalDateTime.parse(timeStr, formatter);
            Instant instant = localDateFormat.atZone(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);
        }catch (Exception e){
            logger.error("再将String类型对象转化为Date类型对象出错",e);
        }
        return null;
    }

    /**
     * 将String类型对象转化为Long类型对象(yyyy-MM-dd HH:mm:ss)
     * @param timeStr
     * @return
     */
    public static Long parseStrToLong(String timeStr){
        try{
            if (timeStr == null || timeStr.length() < 19){
                return null;
            }
            LocalDateTime localDateFormat = LocalDateTime.parse(timeStr, formatter);
            Instant instant = localDateFormat.atZone(ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli();
        }catch (Exception e){
            logger.error("将String类型对象转化为Long类型对象时出错",e);
        }
        return null;
    }

    /**
     * 将Date类型对象转化为Long类型对象
     * @param dateFormat
     * @return
     */
    public static Long parseDateToLong(Date dateFormat){
        try{
            if (dateFormat == null){
                return null;
            }
            Instant instant = dateFormat.toInstant();
            return instant.toEpochMilli();
        }catch (Exception e){
            logger.error("将Date类型对象转化为Long类型对象出错",e);
        }
        return null;
    }

    /**
     * 将Date类型对象转化为String类型对象
     * @param dateFormat
     * @return
     */
    public static String parseDateToStr(Date dateFormat){
        try{
            if (dateFormat == null){
                return null;
            }
            Instant instant = dateFormat.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            LocalDateTime localDateFormat = zonedDateTime.toLocalDateTime();
            return localDateFormat.format(formatter);
        }catch (Exception e){
            logger.error("将Date类型对象转化为String类型对象出错",e);
        }
        return null;
    }

}
