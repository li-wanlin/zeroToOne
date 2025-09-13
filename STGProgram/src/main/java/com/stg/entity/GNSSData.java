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
@TableName("GNSSData")
@NoArgsConstructor
@AllArgsConstructor
public class GNSSData implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    Integer id;

    @TableField(value = "platform_id")
    String platformId;

    @TableField(value = "module_id")
    Integer moduleId;

    @TableField(value = "device_name")
    String deviceName;

    @TableField(value = "time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date time;

    @TableField(value = "target_variation_planeX")
    Double targetVariationPlaneX;

    @TableField(value = "target_variation_planeY")
    Double targetVariationPlaneY;

    @TableField(value = "target_variation_planeH")
    Double targetVariationPlaneH;

    @TableField(value = "reserves1")
    String reserves1;

    @TableField(value = "reserves2")
    String reserves2;

    @TableField(value = "reserves3")
    String reserves3;

    @TableField(value = "reserves4")
    String reserves4;

}
