package com.stg.vo.southVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GNSSReportResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    Meta meta;


    String updateTime;


    List<GNSSReportUnit> units;


}
