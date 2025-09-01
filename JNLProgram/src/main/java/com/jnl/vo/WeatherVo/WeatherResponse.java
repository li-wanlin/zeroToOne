package com.jnl.vo.WeatherVo;


import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    Meta meta;


    String dateTime;


    String skycon;


    Double temperature;



}
