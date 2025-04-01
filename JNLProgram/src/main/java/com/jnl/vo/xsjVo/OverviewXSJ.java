package com.jnl.vo.xsjVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewXSJ {
    String measureId;

    Double measureOrder;

    String measureName;

    String facId;

    String facName;

    Double ctrOrder;

    Double value;

    String unit;

    Double dataType;

    Double length;

    Double decimaldigits;

    Double measureType;

    Double status;

    Double avgValue;

    Long maxTime;

    Double maxValue;

    Long minTime;

    Double minValue;

    List<StatisticXSJ> statistics;
}
