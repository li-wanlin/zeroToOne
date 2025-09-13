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
import java.util.HashMap;
import java.util.Map;


@Data
@TableName("OnenetDev24")
@NoArgsConstructor
@AllArgsConstructor
public class OnenetDev24 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    Long id;


    @TableField(value = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    Date updateTime;


    @TableField(value = "csq")
    Integer csq;


    @TableField(value = "vBat")
    Float vBat;


    @TableField(value = "time")
    Long time;


    @TableField(value = "lv")
    Float lv;

    @TableField(value = "COD")
    Float COD;

    @TableField(value = "DO")
    Float DO;

    @TableField(value = "EC")
    Float EC;

    @TableField(value = "LvTemp")
    Float LvTemp;

    @TableField(value = "NHN")
    Float NHN;

    @TableField(value = "PH")
    Float PH;

    @TableField(value = "ZD")
    Float ZD;




    @TableField(value = "reserves1")
    String reserves1;


    @TableField(value = "reserves2")
    String reserves2;


    @TableField(value = "reserves3")
    String reserves3;



    public Map<String, Double> getFieldMap() {
        Map<String, Double> fieldMap = new HashMap<>();
        fieldMap.put("COD", COD==null?null:Double.valueOf(COD));
        fieldMap.put("DO", DO==null?null:Double.valueOf(DO));
        fieldMap.put("EC", EC==null?null:Double.valueOf(EC));
        fieldMap.put("LvTemp", LvTemp==null?null:Double.valueOf(LvTemp));
        fieldMap.put("NHN", NHN==null?null:Double.valueOf(NHN));
        fieldMap.put("PH", PH==null?null:Double.valueOf(PH));
        fieldMap.put("ZD", ZD==null?null:Double.valueOf(ZD));
        return fieldMap;
    }

    public Double getFieldValue(String fieldName) {
        return getFieldMap().get(fieldName);
    }


}
