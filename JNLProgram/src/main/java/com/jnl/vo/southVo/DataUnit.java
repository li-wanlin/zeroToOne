package com.jnl.vo.southVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    Integer id;

    @JsonProperty("platformId")
    String platformId;

    @JsonProperty("platformName")
    String platformName;

    @JsonProperty("deviceName")
    String deviceName;

    @JsonProperty("showName")
    String showName;

    @JsonProperty("time")
    String time;

    @JsonProperty("statisticsType")
    Integer statisticsType;

    @JsonProperty("sourceLon")
    Double sourceLon;

    @JsonProperty("sourceLat")
    Double sourceLat;

    @JsonProperty("sourceAlt")
    Double sourceAlt;

    @JsonProperty("sourceX")
    Double sourceX;

    @JsonProperty("sourceY")
    Double sourceY;

    @JsonProperty("sourceZ")
    Double sourceZ;

    @JsonProperty("targetPlaneX")
    Double targetPlaneX;

    @JsonProperty("targetPlaneY")
    Double targetPlaneY;

    @JsonProperty("targetPlaneH")
    Double targetPlaneH;

    @JsonProperty("targetVariationPlaneX")
    Double targetVariationPlaneX;

    @JsonProperty("targetVariationPlaneY")
    Double targetVariationPlaneY;

    @JsonProperty("targetVariationPlaneH")
    Double targetVariationPlaneH;

    @JsonProperty("rmsFix")
    Double rmsFix;

    @JsonProperty("hrmsFix")
    Double hrmsFix;

    @JsonProperty("vrmsFix")
    Double vrmsFix;

    @JsonProperty("pdop")
    Double pdop;

    @JsonProperty("hdop")
    Double hdop;

    @JsonProperty("vdop")
    Double vdop;

    @JsonProperty("calculatingType")
    Integer calculatingType;


}
