package com.jnl.vo.memoirsVo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    Integer id;

    String inputTime;

    String inputHapp;

}
