package com.stg.vo.memoirsVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer id;

    String inputTime;

    String inputHapp;

}
