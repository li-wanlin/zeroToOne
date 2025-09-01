package com.jnl.vo.fourPreventVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;




@Data
@AllArgsConstructor
@NoArgsConstructor
public class RainData implements Serializable {

    private static final long serialVersionUID = 1L;


    Double dayAgo;

    Double dayAfter;

    Double dayTwoAfter;

    Double dayThreeAfter;



}
