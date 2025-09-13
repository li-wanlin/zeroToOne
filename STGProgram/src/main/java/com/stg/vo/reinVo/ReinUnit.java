package com.stg.vo.reinVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReinUnit implements Serializable {


    private static final long serialVersionUID = 1L;

    Integer id;


    Integer orderNum;


    /**
     * 开工时间
     */
    String startTime;


    /**
     * 工程名称
     */
    String proName;

    /**
     * 建设内容
     */
    String proContent;


    /**
     * 设计单位
     */
    String designUnit;

    /**
     * 批复单位
     */
    String appUnit;

    /**
     * 施工单位
     */
    String actUnit;

    /**
     * 监理单位
     */
    String manUnit;

    /**
     * 计划时间
     */
    String planTime;

    /**
     * 批复时间
     */
    String finishTime;

    /**
     * 备注
     */
    String proNotes;

}
