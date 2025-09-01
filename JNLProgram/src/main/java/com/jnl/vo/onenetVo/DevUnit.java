package com.jnl.vo.onenetVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevUnit implements Serializable {


    String deviceName;

    Float rainFall;

    Float lv;

    Float lv2;

    Float lv3;

    Float lv4;


}
