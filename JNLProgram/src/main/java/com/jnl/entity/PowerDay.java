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
@TableName("PowerDay")
@AllArgsConstructor
@NoArgsConstructor
public class PowerDay implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "power_one")
    Double powerOne;

    @TableField(value = "power_two")
    Double powerTwo;

    @TableField(value = "power_three")
    Double powerThree;

    @TableField(value = "power_sum")
    Double powerSum;

    @TableField(value = "electric_sum")
    Double electricSum;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;


}
