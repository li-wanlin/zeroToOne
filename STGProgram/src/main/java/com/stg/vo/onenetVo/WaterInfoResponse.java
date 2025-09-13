package com.stg.vo.onenetVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterInfoResponse implements Serializable {


    Meta meta;

    String updateTime;


    /**
     * 水位
     */
    Double lv;

    /**
     * 化学需氧量
     */
    Double COD;

    /**
     * 溶解氧
     */
    Double DO;

    /**
     * 电导率
     */
    Double EC;

    /**
     * 水温
     */
    Double LvTemp;

    /**
     * 氨氮
     */
    Double NHN;

    /**
     * 酸碱度PH
     */
    Double PH;

    /**
     * 浊度
     */
    Double ZD;





}
