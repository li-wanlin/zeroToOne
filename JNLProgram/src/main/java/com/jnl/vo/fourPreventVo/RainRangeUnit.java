package com.jnl.vo.fourPreventVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainRangeUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    String rainTime;


    Double value;

}
