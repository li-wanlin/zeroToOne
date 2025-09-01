package com.jnl.vo.onenetVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainUnit {

    String device;


    Date updateTime;


    Float value;

}
