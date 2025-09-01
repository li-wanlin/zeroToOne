package com.jnl.vo.onenetVo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略未知字段
public class OnenetSubData {

    String deviceId;

    String deviceName;

    OnenetParams params;

    String productId;

}
