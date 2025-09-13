package com.stg.vo.emerVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmerUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;


    Integer orderNum;


    /**
     * 类型
     */
    String supType;


    /**
     * 年份
     */
    String supYear;


    /**
     * 批复单位
     */
    String appUnit;


    /**
     * 状态（已报批/已上报/未开展）
     */
    String supState;


    /**
     * 备注
     */
    String proNotes;

}
