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
@TableName("ProtectManage")
@AllArgsConstructor
@NoArgsConstructor
public class ProtectManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "protect_type")
    String protectType;


    @TableField(value = "protect_name")
    String protectName;


    @TableField(value = "plan_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date planTime;


    @TableField(value = "act_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date actTime;


    @TableField(value = "plan_amount")
    Double planAmount;


    @TableField(value = "act_amount")
    Double actAmount;


    @TableField(value = "remain_pro")
    String remainPro;


    @TableField(value = "development")
    String development;


    @TableField(value = "reform_state")
    String reformState;


    @TableField(value = "reform_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date reformTime;


    @TableField(value = "leader_info")
    String leaderInfo;


    @TableField(value = "pro_notes")
    String proNotes;


    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "reserves1")
    String reserves1;

}
