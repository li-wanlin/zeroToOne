package com.stg.vo.patrolVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProPatrolResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    Integer totalPage;

    Integer totalCount;

    Integer currentPage;

    /**
     * 计划巡查次数
     */
    Integer planCount;

    /**
     * 实际完成巡查次数
     */
    Integer finishCount;

    /**
     * 隐患完结数量
     */
    Integer completeCount;

    /**
     * 隐患待处理数量
     */
    Integer pendingCount;


    List<ProPatrolUnit> proPatrolUnits;


}
