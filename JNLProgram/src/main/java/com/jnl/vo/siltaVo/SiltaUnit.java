package com.jnl.vo.siltaVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiltaUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;

    Integer orderNum;

    /**
     * 开始时间
     */
    String stTime;

    /**
     * 计划工期
     */
    String planDuration;

    /**
     * 完工时间
     */
    String endTime;

    /**
     * 计划费用
     */
    Double planAmount;

    /**
     * 实际费用
     */
    Double actAmount;

    /**
     * 实施单位
     */
    String actUnit;

    /**
     * 批复单位
     */
    String appUnit;


}
