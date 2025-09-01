package com.jnl.vo.southVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SouthLoginVo {

    String info;

    String error;

    Integer status;

    Boolean success;

    SouthLoginData data;


}
