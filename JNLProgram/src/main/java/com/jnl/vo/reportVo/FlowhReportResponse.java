package com.jnl.vo.reportVo;


import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowhReportResponse implements Serializable {


    Meta meta;

    Integer totalPage;

    Integer totalCount;

    Integer currentPage;

    List<FlowUnit> units;


}
