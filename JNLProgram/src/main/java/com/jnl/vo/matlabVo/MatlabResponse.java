package com.jnl.vo.matlabVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatlabResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    Meta meta;


    String updateTime;


    Double flag;



    List<ResUnit> rycs;


    List<ResUnit> xajfs;


    List<ResUnit> skfhStage;


    List<ResUnit> skfhFlow;


    List<ResUnit> hsyjLwz;


    List<ResUnit> hsyjDqg;


    List<ResUnit> hsyjByl;



}
