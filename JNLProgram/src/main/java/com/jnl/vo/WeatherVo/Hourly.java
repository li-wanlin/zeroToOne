package com.jnl.vo.WeatherVo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class Hourly {

    String status;

    String description;

    List<PreUnit> precipitation;

    List<TemUnit> temperature;

}
