package com.stg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateLocalUtils {

    //以下操作皆为线程安全
    private static final String formatOne = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
     * 获取给定时间和格式的日期字符串
     * @param time
     * @param formatStr
     * @return
     */
    public static String getGiveTimeToStr(LocalDateTime time,String formatStr){
        try {
            if (time == null || formatStr.length() < 10){
                return null;
            }
            DateTimeFormatter patternStr = DateTimeFormatter.ofPattern(formatStr);
            return time.format(patternStr);

        }catch (Exception e){
            logger.error("获取日期指定格式出错",e);
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
     * 将Date类型对象转化为String类型对象(yyyy-MM-dd HH:mm:ss)
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


    /**
     * 将Date类型对象转化为String类型对象(yyyy-MM-dd)
     * @param dateFormat
     * @return
     */
    public static String parseDateToStrTwo(Date dateFormat){
        try{
            if (dateFormat == null){
                return null;
            }
            Instant instant = dateFormat.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            LocalDateTime localDateFormat = zonedDateTime.toLocalDateTime();
            return localDateFormat.format(dateFormatter);
        }catch (Exception e){
            logger.error("将Date类型对象转化为String类型对象出错",e);
        }
        return null;
    }



    /**
     * 将Date类型对象转换为指定格式String类型对象
     * @param dateFormat
     * @param format
     * @return
     */
    public static String parseGiveDaToStr(Date dateFormat,String format){
        try{
            if (dateFormat == null){
                return null;
            }
            Instant instant = dateFormat.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            LocalDateTime localDateFormat = zonedDateTime.toLocalDateTime();

            DateTimeFormatter formatLocal = DateTimeFormatter.ofPattern(format);
            return localDateFormat.format(formatLocal);

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


    /**
     * 将String类型对象转化为Date类型对象(yyyy-MM-dd)
     * @param timeStr
     * @return
     */
    public static Date parseGiveStrToDate(String timeStr){
        try {
            if (timeStr == null || timeStr.length() < 10){
                return null;
            }
            timeStr = timeStr.substring(0,10);
            LocalDate localDate = LocalDate.parse(timeStr, dateFormatter);

            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);
        }catch (Exception e){
            logger.error("再将String类型对象转化为Date类型对象出错",e);
        }
        return null;
    }


    /**
     * 将LocalDateTime转化为Date类型
     * @param time
     * @return
     */
    public static Date parseTimeToDate(LocalDateTime time){
        try {
            if (time == null){
                return null;
            }
            return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        }catch (Exception e){
            logger.error("将LocalDateTime转化为Date类型出错",e);
        }
        return null;
    }


    /**
     * 将Date类型对象转为LocalDateTIme类型对象
     * @param date
     * @return
     */
    public static LocalDateTime parseDateToTime(Date date){
        try {
            if (date == null){
                return null;
            }

            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }


    /**
     * 获取某天最后时间
     * @param dateStr
     * @return
     */
    public static Date parseStrToEnd(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 9){
                return null;
            }

            LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);

            LocalTime endOfDay = LocalTime.of(23, 59, 59);

            return Date.from(LocalDateTime.of(localDate,endOfDay).atZone(ZoneId.systemDefault()).toInstant());

        }catch (Exception e){
            logger.error("获取给定天最后时间出错",e);
        }
        return null;
    }


    /**
     * 获取某天起始时间
     * @param dateStr
     * @return
     */
    public static Date parseStrToStart(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 9){
                return null;
            }

            LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);

            LocalDateTime startOfDay = localDate.atStartOfDay();


            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);

        }catch (Exception e){
            logger.error("获取给定天起始时间出错",e);
        }
        return null;
    }



    /**
     * 获取某年的第一天
     * @param dateStr
     * @return
     */
    public static Date parseYearToStart(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 4){
                return null;
            }
            LocalDate date = LocalDate.of(Integer.parseInt(dateStr), 1, 1);
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        }catch (Exception e){
            logger.error("获取某年第一天出错",e);
        }
        return null;
    }


    /**
     * 获取某年的最后一天
     * @param dateStr
     * @return
     */
    public static Date parseYearToEnd(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 4){
                return null;
            }
            LocalDate date = LocalDate.of(Integer.parseInt(dateStr), 12, 31);
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        }catch (Exception e){
            logger.error("获取某年第一天出错",e);
        }
        return null;
    }


    /**
     * 获取某月第一天
     * @param dateStr
     * @return
     */
    public static LocalDate parseMonthToStart(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 10){
                return null;
            }


            LocalDate month = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);


            return month.withDayOfMonth(1);

        }catch (Exception e){
            logger.error("获取某月第一天出错",e);
        }
        return null;
    }


    /**
     * 获取某月最后一天
     * @param dateStr
     * @return
     */
    public static LocalDate parseMonthToEnd(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 10){
                return null;
            }

            LocalDate month = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            return month.withDayOfMonth(month.lengthOfMonth());

        }catch (Exception e){
            logger.error("获取某月最后一天出错",e);
        }
        return null;
    }


    /**
     * 将字符串转化为LocalDateTime（yyyy-MM-dd HH:mm:ss）
     * @param dateStr
     * @return
     */
    public static LocalDateTime parseStrToTime(String dateStr){
        try {
            if (dateStr == null || dateStr.length() < 13){
                return null;
            }

            return LocalDateTime.parse(dateStr,formatter);

        }catch (Exception e){
            logger.error("",e);
        }
        return null;
    }

    /**
     * 将LocalDateTime转化为字符串（yyyy-MM-dd HH:mm:ss）
     * @param time
     * @return
     */
    public static String parseTimeToStr(LocalDateTime time){
        return time.format(formatter);
    }



    /**
     * 将LocalDateTime转化为字符串（yyyy-MM-dd）
     * @param time
     * @return
     */
    public static String parseTimeToStrTwo(LocalDateTime time){
        return time.format(dateFormatter);
    }



    /**
     * 将LocalDate对象转为Date
     * @param time
     * @return
     */
    public static Date parseLocalDateToDateStart(LocalDate time){
        return Date.from(time.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    /**
     * 将LocalDate对象转为Date
     * @param time
     * @return
     */
    public static Date parseLocalDateToDateEnd(LocalDate time){
        return Date.from(time.atTime(LocalTime.MAX).withNano(0).atZone(ZoneId.systemDefault()).toInstant());
    }

}
