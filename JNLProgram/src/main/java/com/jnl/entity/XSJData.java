package com.jnl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("XSJData")
@NoArgsConstructor
@AllArgsConstructor
public class XSJData implements Serializable {


    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "time")
    Date time;

    @TableField(value = "sec")
    Long sec;

    @TableField(value = "stage")
    Double stage;

    @TableField(value = "power_sum")
    Double powerSum;

    @TableField(value = "power_one")
    Double powerOne;

    @TableField(value = "power_two")
    Double powerTwo;

    @TableField(value = "power_three")
    Double powerThree;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

    @TableField(value = "reserves3")
    String reserves3;

    @TableField(value = "reserves4")
    String reserves4;


    /**
     * 当日累积电量
     */
    @TableField(exist = false)
    Integer accrue;


    /**
     * 当前库容
     */
    @TableField(exist = false)
    Integer capacity;



}
