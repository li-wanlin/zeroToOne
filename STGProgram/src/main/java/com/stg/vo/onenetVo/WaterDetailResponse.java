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
public class WaterDetailResponse implements Serializable {

    Meta meta;


    List<WaterUnit> waterUnits;



}
