package com.stg.vo.floodVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FloodUnit implements Serializable {


    private static final long serialVersionUID = 1L;


    Integer id;


    Integer orderNum;


    /**
     * 时间
     */
    String fpTime;


    /**
     * 类别
     */
    String fpType;


    /**
     * 单位
     */
    String fpUnit;


    /**
     * 存放位置
     */
    String fpLocation;


    /**
     * 入库
     */
    String fpStore;


    /**
     * 入库经办人
     */
    String storeOperator;


    /**
     * 出库
     */
    String fpOutbound;


    /**
     * 出库经办人
     */
    String outOperator;


    /**
     * 存量
     */
    String fpStock;


    /**
     * 备注
     */
    String proNotes;

}
