package com.jnl.vo.patrolVo;


import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EduImgResponse implements Serializable {

    private static final long serialVersionUID = 1L;


    Meta meta;



    List<String> fileNames;

}
