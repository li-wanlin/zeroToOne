package com.jnl.vo.seepageVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeepageUnit implements Serializable {


    private static final long serialVersionUID = 1L;

    String inputTime;

    Double value;


}
