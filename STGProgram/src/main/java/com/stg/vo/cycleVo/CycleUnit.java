package com.stg.vo.cycleVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CycleUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;


    Integer orderNum;


    /**
     * 设备名称
     */
    String deviceName;

    /**
     * 设备型号
     */
    String deviceModel;

    /**
     * 位置
     */
    String deviceLo;

    /**
     * 安装时间
     */
    String installTime;

    /**
     * 设备评级
     */
    String deviceLv;

    /**
     * 报废时间
     */
    String scrapTime;

    /**
     * 责任人
     */
    String deviceLeader;

    /**
     * 备注
     */
    String proNotes;


}
