package com.jnl.vo.reportVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer orderNum;

    String updateTime;

    Double lv;

    Double lv2;

    Double lv3;

    Double lv4;

    Double lv5;

    Double lv6;

}
