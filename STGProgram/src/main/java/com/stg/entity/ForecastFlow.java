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
@TableName("ForecastFlow")
@AllArgsConstructor
@NoArgsConstructor
public class ForecastFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date time;

    @TableField(value = "five_day_flow")
    Double fiveDayFlow;


    @TableField(value = "four_day_flow")
    Double fourDayFlow;


    @TableField(value = "three_day_flow")
    Double threeDayFlow;


    @TableField(value = "two_day_flow")
    Double twoDayFlow;


    @TableField(value = "one_day_flow")
    Double oneDayFlow;


    @TableField(value = "two_day_rain")
    Double twoDayRain;


    @TableField(value = "one_day_rain")
    Double oneDayRain;


    @TableField(value = "now_rain")
    Double nowRain;

    @TableField(value = "forecast")
    Double forecast;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;



}
