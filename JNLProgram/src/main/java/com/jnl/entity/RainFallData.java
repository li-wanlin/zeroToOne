package com.jnl.entity;

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
@TableName("RainFallData")
@AllArgsConstructor
@NoArgsConstructor
public class RainFallData implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "input_time",type = IdType.NONE)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date inputTime;


    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "rainfall1")
    Float rainfall1;


    @TableField(value = "rainfall2")
    Float rainfall2;


    @TableField(value = "rainfall3")
    Float rainfall3;


    @TableField(value = "rainfall4")
    Float rainfall4;


    @TableField(value = "rainfall5")
    Float rainfall5;


    @TableField(value = "average_rain")
    Float averageRain;


    @TableField(value = "average_day")
    Double averageDay;


    @TableField(value = "reserves1")
    String reserves1;


    @TableField(value = "reserves2")
    String reserves2;



}
