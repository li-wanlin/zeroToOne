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
@TableName("WaterDay")
@AllArgsConstructor
@NoArgsConstructor
public class WaterDay implements Serializable {


    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "COD")
    Double COD;

    @TableField(value = "DO")
    Double DO;

    @TableField(value = "EC")
    Double EC;

    @TableField(value = "LvTemp")
    Double LvTemp;

    @TableField(value = "NHN")
    Double NHN;

    @TableField(value = "PH")
    Double PH;

    @TableField(value = "ZD")
    Double ZD;



    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;


}
