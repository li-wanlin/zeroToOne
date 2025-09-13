package com.stg.vo.reportVo;


import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StagedReportResponse implements Serializable {

    Meta meta;

    Integer totalPage;

    Integer totalCount;

    Integer currentPage;


    List<StageUnit> units;


}
