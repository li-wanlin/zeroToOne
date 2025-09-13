package com.stg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("WeatherForecast")
@AllArgsConstructor
@NoArgsConstructor
public class WeatherForecast implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "date_time",type = IdType.NONE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date dateTime;


    @TableField(value = "precipitation")
    Double precipitation;


    @TableField(value = "temperature")
    Double temperature;


    @TableField(value = "average_day")
    Double averageDay;


    @TableField(value = "reserves1")
    String reserves1;


    @TableField(value = "reserves2")
    String reserves2;



}
