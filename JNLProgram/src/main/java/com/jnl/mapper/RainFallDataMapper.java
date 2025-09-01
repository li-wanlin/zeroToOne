package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.RainFallData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RainFallDataMapper extends BaseMapper<RainFallData> {



    @Update("<script>" +
            "INSERT INTO `RainFallData` (`input_time`, `rainfall1`, `rainfall2`, `rainfall3`, `rainfall4`, `rainfall5`, `average_rain`, `average_day`, `update_time`) " +
            "VALUES " +
            "<foreach collection=\"list\" item=\"item\" separator=\",\">" +
            "(#{item.inputTime}, #{item.rainfall1}, #{item.rainfall2}, #{item.rainfall3}, #{item.rainfall4}, #{item.rainfall5}, #{item.averageRain}, #{item.averageDay}, #{item.updateTime})" +
            "</foreach>" +
            "ON DUPLICATE KEY UPDATE " +
            "`rainfall1` = VALUES(`rainfall1`), " +
            "`rainfall2` = VALUES(`rainfall2`), " +
            "`rainfall3` = VALUES(`rainfall3`), " +
            "`rainfall4` = VALUES(`rainfall4`), " +
            "`rainfall5` = VALUES(`rainfall5`), " +
            "`average_day` = VALUES(`average_day`), " +
            "`update_time` = VALUES(`update_time`), " +
            "`average_rain` = VALUES(`average_rain`)" +
            "</script>")
    Boolean batchInsertOrUpdate(@Param("list") List<RainFallData> list);
}
