package com.stg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stg.entity.WeatherForecast;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface WeatherMapper extends BaseMapper<WeatherForecast> {



    @Update("<script>" +
            "INSERT INTO `WeatherForecast` (`date_time`, `precipitation`, `temperature`, `reserves1`, `reserves2`) " +
            "VALUES " +
            "<foreach collection=\"list\" item=\"item\" separator=\",\">" +
            "(#{item.dateTime}, #{item.precipitation}, #{item.temperature}, #{item.reserves1}, #{item.reserves2})" +
            "</foreach>" +
            "ON DUPLICATE KEY UPDATE " +
            "`precipitation` = VALUES(`precipitation`), " +
            "`temperature` = VALUES(`temperature`), " +
            "`reserves1` = VALUES(`reserves1`), " +
            "`reserves2` = VALUES(`reserves2`)" +
            "</script>")
    Boolean batchInsertOrUpdate(@Param("list") List<WeatherForecast> list);


}
