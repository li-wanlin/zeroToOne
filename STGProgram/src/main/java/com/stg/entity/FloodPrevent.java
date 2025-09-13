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
@TableName("FloodPrevent")
@AllArgsConstructor
@NoArgsConstructor
public class FloodPrevent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    @TableField(value = "fp_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date fpTime;

    @TableField(value = "fp_type")
    String fpType;

    @TableField(value = "fp_unit")
    String fpUnit;

    @TableField(value = "fp_location")
    String fpLocation;

    @TableField(value = "fp_store")
    String fpStore;

    @TableField(value = "store_operator")
    String storeOperator;

    @TableField(value = "fp_outbound")
    String fpOutbound;

    @TableField(value = "out_operator")
    String outOperator;

    @TableField(value = "fp_stock")
    String fpStock;

    @TableField(value = "pro_notes")
    String proNotes;

    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;

    @TableField(value = "reserves1")
    String reserves1;






}
