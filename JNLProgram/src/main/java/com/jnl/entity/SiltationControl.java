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
@TableName("SiltationControl")
@AllArgsConstructor
@NoArgsConstructor
public class SiltationControl implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "st_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date stTime;

    @TableField(value = "plan_duration")
    String planDuration;

    @TableField(value = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date endTime;

    @TableField(value = "plan_amount")
    Double planAmount;

    @TableField(value = "act_amount")
    Double actAmount;

    @TableField(value = "act_unit")
    String actUnit;

    @TableField(value = "app_unit")
    String appUnit;

    @TableField(value = "file_name")
    String fileName;

    @TableField(value = "file_path")
    String filePath;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "reserves1")
    String reserves1;


}
