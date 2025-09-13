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
@TableName("GNSSHour")
@AllArgsConstructor
@NoArgsConstructor
public class GNSSHour implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "m1X")
    Double m1X;

    @TableField(value = "m1Y")
    Double m1Y;

    @TableField(value = "m1H")
    Double m1H;

    @TableField(value = "m2X")
    Double m2X;

    @TableField(value = "m2Y")
    Double m2Y;


    @TableField(value = "m2H")
    Double m2H;

    @TableField(value = "m3X")
    Double m3X;

    @TableField(value = "m3Y")
    Double m3Y;

    @TableField(value = "m3H")
    Double m3H;



    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

}
