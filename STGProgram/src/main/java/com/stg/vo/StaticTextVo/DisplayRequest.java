package com.stg.vo.StaticTextVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    String display;

    List<String> assessments;

    List<String> safetyMan;

    List<Double> amountInfos;


}
