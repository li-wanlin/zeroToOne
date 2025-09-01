package com.jnl.vo.protectVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtectResponse implements Serializable {


    private static final long serialVersionUID = 1L;

    Meta meta;

    Integer totalPage;

    Integer totalCount;

    Integer currentPage;


    /**
     * 年度工程数量
     */
    Integer yearCount;


    /**
     * 年度计划投资
     */
    Double planCount;


    List<ProtectUnit> protectUnits;

}
