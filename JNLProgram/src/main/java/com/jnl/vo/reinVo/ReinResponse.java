package com.jnl.vo.reinVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReinResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    Integer totalPage;

    Integer totalCount;

    Integer currentPage;


    List<ReinUnit> reinUnits;

}
