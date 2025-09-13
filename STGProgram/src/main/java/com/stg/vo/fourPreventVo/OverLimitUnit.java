package com.stg.vo.fourPreventVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverLimitUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer orderNum;

    String updateTime;

    String location;

    String device;

    Double lv;

    String limitType;

    String limitState;


}
