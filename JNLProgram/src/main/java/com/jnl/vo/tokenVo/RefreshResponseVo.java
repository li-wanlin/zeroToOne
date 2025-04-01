package com.jnl.vo.tokenVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    RefreshDataVo data;


}
