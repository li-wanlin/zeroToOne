package com.stg.vo.onenetVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainReportUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    String inputTime;


    Double rainFall1;

    Double rainFall2;





}
