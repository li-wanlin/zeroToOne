package com.jnl.vo.reportVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer orderNum;


    String updateTime;

    Double rainfall1;

    Double rainfall2;

    Double rainfall3;

    Double rainfall4;

    Double rainfall5;


}
