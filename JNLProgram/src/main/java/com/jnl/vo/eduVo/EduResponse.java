package com.jnl.vo.eduVo;

import com.jnl.entity.EduTrain;
import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EduResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    Integer totalPage;

    Integer totalCount;

    Integer currentPage;

    List<EduTrain> eduTrains;


}
