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
@TableName("StageHour")
@AllArgsConstructor
@NoArgsConstructor
public class StageHour implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "lv")
    Double lv;

    @TableField(value = "lv2")
    Double lv2;

    @TableField(value = "lv3")
    Double lv3;

    @TableField(value = "lv4")
    Double lv4;

    @TableField(value = "lv5")
    Double lv5;

    @TableField(value = "lv6")
    Double lv6;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;


}
