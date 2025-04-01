package com.jnl.vo.xsjVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticXSJ {

    String measureId;

    Long sec;

    Double statisticalType;

    Double realTimeValue;

    Double oldValue;

    Double oldMeterValue;

    Double calculatedType;

    Double calculatesValue;

    Double plannedValue;

    Double maxValue;

    Long maxTime;

    Double minValue;

    Long minTime;

}
