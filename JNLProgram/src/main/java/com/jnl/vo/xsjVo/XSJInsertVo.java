package com.jnl.vo.xsjVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XSJInsertVo {


    Date time;

    Long sec;

    Double stage;

    Double powerSum;

    Double powerOne;

    Double powerTwo;

    Double powerThree;
}
