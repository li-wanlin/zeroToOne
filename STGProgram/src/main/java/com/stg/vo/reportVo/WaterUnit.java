package com.stg.vo.reportVo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer orderNum;

    String updateTime;


    Double COD;


    Double DO;


    Double EC;


    Double LvTemp;


    Double NHN;


    Double PH;


    Double ZD;





}
