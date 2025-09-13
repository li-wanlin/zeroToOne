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
@TableName("OnenetDev23")
@NoArgsConstructor
@AllArgsConstructor
public class OnenetDev23 implements Serializable {


    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Long id;


    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "csq")
    Integer csq;


    @TableField(value = "vBat")
    Float vBat;


    @TableField(value = "time")
    Long time;


    @TableField(value = "lv")
    Float lv;


    @TableField(value = "reserves1")
    String reserves1;


    @TableField(value = "reserves2")
    String reserves2;


    @TableField(value = "reserves3")
    String reserves3;

}
