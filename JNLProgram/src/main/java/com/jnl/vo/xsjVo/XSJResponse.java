package com.jnl.vo.xsjVo;

import com.jnl.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XSJResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

    //水位
    Double stage;

    //总出力，单位千瓦
    Integer powerSum;

    //1号机组出力
    Integer powerOne;

    //2号机组出力
    Integer powerTwo;

    //3号机组出力
    Integer powerThree;

    //当日累计电量，单位千瓦时
    Integer accrue;

    //当前库容，单位万m³
    Integer capacity;

    //获取最新一条数据时间
    String time;




}
