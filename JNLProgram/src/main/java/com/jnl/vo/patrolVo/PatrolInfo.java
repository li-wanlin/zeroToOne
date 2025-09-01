package com.jnl.vo.patrolVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatrolInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer id;

    /**
     * 每页序号
     */
    Integer orderNum;

    /**
     * 巡查时间
     */
    String patrolTime;

    /**
     * 巡查类型
     */
    String patrolType;

    /**
     * 巡查地点或路线
     */
    String patrolArea;

    /**
     * 巡查情况
     */
    String patrolState;

    /**
     * 巡查问题
     */
    String patrolProblem;

    /**
     * 应对措施
     */
    String solutions;

    /**
     * 巡查负责人
     */
    String patrolLeader;

    /**
     * 巡查人员
     */
    String patrolPerson;

    /**
     * 整改是否完成
     */
    String ifComplete;


    /**
     * 整改完成情况
     */
    String completeState;

    /**
     * 备注
     */
    String patrolNotes;





}
