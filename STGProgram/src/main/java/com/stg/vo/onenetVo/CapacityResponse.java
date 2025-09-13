package com.stg.vo.onenetVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapacityResponse implements Serializable {
    
    
    Meta meta;
    

    String updateTime;


    Double capacity;


    Double lv;

    
    
}
