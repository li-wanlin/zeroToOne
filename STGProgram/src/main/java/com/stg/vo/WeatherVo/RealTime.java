package com.stg.vo.WeatherVo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class RealTime implements Serializable {

    private static final long serialVersionUID = 1L;


    String status;

    Double temperature;

    Double humidity;

    Double cloudrate;

    String skycon;


}
