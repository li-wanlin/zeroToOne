package com.stg.vo.loginVo;

import com.stg.vo.functionVo.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    Meta meta;

}
