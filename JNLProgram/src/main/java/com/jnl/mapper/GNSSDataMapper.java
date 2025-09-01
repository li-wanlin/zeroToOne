package com.jnl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jnl.entity.GNSSData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GNSSDataMapper extends BaseMapper<GNSSData> {


    @Select("SELECT * FROM (\n" +
            "    SELECT *, \n" +
            "    ROW_NUMBER() OVER(PARTITION BY device_name ORDER BY time DESC) as rn \n" +
            "    FROM GNSSData \n" +
            "    WHERE device_name IN ('牛岭水库M1', '牛岭水库M2', '牛岭水库M3') \n" +
            ") t WHERE rn = 1;")
    List<GNSSData> selectLatestGNSS();
}
