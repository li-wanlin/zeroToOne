package com.stg.vo.southVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.stg.task.DataStatisticDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GNSSReceive implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonDeserialize(using = DataStatisticDeserializer.class)
    DataStatistic data;

    @JsonProperty("error")
    String error;

    @JsonProperty("status")
    Integer status;

    @JsonProperty("info")
    String info;

}
