package com.jnl.vo.matlabVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    String time;


    Double value;


}
