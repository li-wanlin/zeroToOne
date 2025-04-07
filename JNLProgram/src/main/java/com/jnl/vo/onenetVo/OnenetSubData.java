package com.jnl.vo.onenetVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnenetSubData {

    String deviceId;

    String deviceName;

    OnenetParams params;

    String productId;

}
