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
@TableName("AnnualRepair")
@AllArgsConstructor
@NoArgsConstructor
public class AnnualRepair implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "plan_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date planTime;

    @TableField(value = "plan_content")
    String planContent;

    @TableField(value = "plan_amount")
    Double planAmount;

    @TableField(value = "app_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date appTime;

    @TableField(value = "app_unit")
    String appUnit;

    @TableField(value = "app_amount")
    Double appAmount;

    @TableField(value = "development")
    String development;

    @TableField(value = "dep_amount")
    Double depAmount;

    @TableField(value = "remain_pro")
    String remainPro;

    @TableField(value = "pro_notes")
    String proNotes;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "reserves1")
    String reserves1;

}
