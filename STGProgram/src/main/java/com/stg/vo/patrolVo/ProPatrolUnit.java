package com.stg.vo.patrolVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProPatrolUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;

    /**
     * 每页序号
     */
    Integer orderNum;


    /**
     * 巡查区域
     */
    String patrolArea;

    /**
     * 巡查计划时间
     */
    String planTime;


    /**
     * 实际完成时间
     */
    String finishTime;

    /**
     * 巡查情况
     */
    String patrolState;


    /**
     * 整改情况
     */
    String reformState;


    /**
     * 整改时间
     */
    String reformTime;


    /**
     * 整改是否完成
     */
    String ifReform;



    /**
     * 巡查人员
     */
    String patrolPerson;


    /**
     * 责任人及联系方式
     */
    String leaderInfo;


    /**
     * 备注
     */
    String patrolNotes;

    


}
