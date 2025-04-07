package com.jnl.vo.onenetVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnenetMsg {

    String msgType;

    OnenetSubData subData;

}
