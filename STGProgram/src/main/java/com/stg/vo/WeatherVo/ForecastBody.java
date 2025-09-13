package com.stg.vo.WeatherVo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class ForecastBody implements Serializable {

    private static final long serialVersionUID = 1L;

    String status;

    @JsonProperty("api_version")
    String apiVersion;

    @JsonProperty("api_status")
    String apiStatus;
    String lang;
    String unit;
    Integer tzshift;
    String timezone;
    Long serverTime;
    List<Double> location;

    ForeResult result;

}
