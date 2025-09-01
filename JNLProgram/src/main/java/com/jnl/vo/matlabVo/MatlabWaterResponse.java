package com.jnl.vo.matlabVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatlabWaterResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    Meta meta;


    Double averageFlow;


    Double powerFlow;


    Double floodFlow;




}
