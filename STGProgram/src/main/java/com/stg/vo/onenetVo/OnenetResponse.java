package com.stg.vo.onenetVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnenetResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;


    List<RainUnit>  rainUnits;


    List<DevUnit> devUnits;

    List<RainHisUnit> rainHisUnits;

}
