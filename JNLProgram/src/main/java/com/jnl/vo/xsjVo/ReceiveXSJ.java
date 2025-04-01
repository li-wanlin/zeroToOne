package com.jnl.vo.xsjVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveXSJ {
    Integer status;

    List<OverviewXSJ> data;

    String msg;
}
