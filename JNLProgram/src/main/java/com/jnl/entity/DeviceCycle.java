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
@TableName("DeviceCycle")
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCycle implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id",type = IdType.AUTO)
    Integer id;

    @TableField(value = "device_name")
    String deviceName;

    @TableField(value = "device_model")
    String deviceModel;

    @TableField(value = "device_lo")
    String deviceLo;

    @TableField(value = "install_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date installTime;

    @TableField(value = "device_lv")
    String deviceLv;

    @TableField(value = "scrap_time")
    Date scrapTime;

    @TableField(value = "device_leader")
    String deviceLeader;

    @TableField(value = "pro_notes")
    String proNotes;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "reserves1")
    String reserves1;



}
