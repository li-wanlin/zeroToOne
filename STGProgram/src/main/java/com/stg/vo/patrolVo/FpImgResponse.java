package com.stg.vo.patrolVo;


import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FpImgResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;


    List<String> fileNames;

}
