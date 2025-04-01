package com.jnl.vo.loginVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginData {

    String username;

    String nickname;

    String accessToken;

    String refreshToken;


}
