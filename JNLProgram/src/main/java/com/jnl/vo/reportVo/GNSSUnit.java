package com.jnl.vo.reportVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GNSSUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer orderNum;


    String updateTime;

    Double m1X;

    Double m1Y;

    Double m1H;

    Double m2X;

    Double m2Y;

    Double m2H;

    Double m3X;

    Double m3Y;

    Double m3H;


}
