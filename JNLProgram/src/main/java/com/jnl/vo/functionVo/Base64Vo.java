package com.jnl.vo.functionVo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Base64Vo {
    String sub;

    Long iat;

    Long exp;
}
