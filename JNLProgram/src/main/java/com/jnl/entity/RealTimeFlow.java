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
@TableName("RealTimeFlow")
@AllArgsConstructor
@NoArgsConstructor
public class RealTimeFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "date_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date dateTime;

    @TableField(value = "power")
    Double power;

    @TableField(value = "flood")
    Double flood;

    @TableField(value = "average_power")
    Double averagePower;

    @TableField(value = "average_flood")
    Double averageFlood;


    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

}
