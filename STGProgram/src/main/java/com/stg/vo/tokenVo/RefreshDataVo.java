package com.stg.vo.tokenVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshDataVo {

    String accessToken;

    String refreshToken;


}
