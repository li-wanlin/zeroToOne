package com.jnl.vo.southVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataStatistic implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("list")
    DataList list;

    @JsonProperty("latest")
    DataLatest latest;

}
