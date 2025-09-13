package com.stg.vo.damSignVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUnit implements Serializable {

    private static final long serialVersionUID = 1L;


    Integer id;

    /**
     * 信号位，1代表塌坝，2代表不塌坝
     */
    String sign;






}
