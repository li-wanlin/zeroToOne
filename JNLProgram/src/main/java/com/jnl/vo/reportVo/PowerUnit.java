package com.jnl.vo.reportVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer orderNum;


    String updateTime;

    Double powerOne;

    Double powerTwo;

    Double powerThree;

    Double powerSum;

    Double electricSum;


}
