package com.jnl.vo.southVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GNSSReportUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    String inputTime;


    Double oneX;

    Double oneY;

    Double oneH;


    Double twoX;

    Double twoY;

    Double twoH;

    Double threeX;

    Double threeY;

    Double threeH;




}
