package com.jnl.vo.onenetVo;

import com.jnl.vo.fourPreventVo.RainRangeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainHisUnit implements Serializable {


    private static final long serialVersionUID = 1L;


    String deviceName;

    Double maxRain;

    Double sumRain;

    List<RainRangeUnit>  rainUnits;


}
