package com.stg.vo.onenetVo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class OnenetParams {

    @JsonProperty("CSQ")
    OnenetBase csq;

    @JsonProperty("RainFall")
    OnenetBase rainFall;

    @JsonProperty("VBat")
    OnenetBase vbat;

    @JsonProperty("Lv")
    OnenetBase lv;

    @JsonProperty("Lv2")
    OnenetBase lv2;

    @JsonProperty("Lv3")
    OnenetBase lv3;

    @JsonProperty("Lv4")
    OnenetBase lv4;

}
