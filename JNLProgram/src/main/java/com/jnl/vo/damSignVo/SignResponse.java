package com.jnl.vo.damSignVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignResponse implements Serializable {


    private static final long serialVersionUID = 1L;

    Meta meta;

    SignUnit unit;


}
