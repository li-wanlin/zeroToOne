package com.stg.vo.southVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GNSSUnit {
    String time;

    Double targetVariationPlaneX;

    Double targetVariationPlaneY;

    Double targetVariationPlaneH;
}
