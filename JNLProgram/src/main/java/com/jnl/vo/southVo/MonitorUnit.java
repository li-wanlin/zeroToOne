package com.jnl.vo.southVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorUnit {

    String deviceName;


    GNSSUnit gnssUnit;


    List<GNSSUnit> gnssUnits;

}
