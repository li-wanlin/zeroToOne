package com.jnl.vo.capacityVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapaUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;

    Integer orderNum;

    /**
     * 时间
     */
    String caTime;

    /**
     * 编制单位
     */
    String caUnit;

    /**
     * 批复单位
     */
    String appUnit;

    /**
     * 备注
     */
    String proNotes;
}
