package com.stg.vo.matlabVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkwResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;


    ResUnit unit;


}
