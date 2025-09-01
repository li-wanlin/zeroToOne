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
@TableName("SeepageDay")
@AllArgsConstructor
@NoArgsConstructor
public class SeepageDay implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "lv")
    Double lv;

    @TableField(value = "lv2")
    Double lv2;

    @TableField(value = "lv3")
    Double lv3;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;


}
