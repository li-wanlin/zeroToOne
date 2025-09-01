package com.jnl.service;

import com.jnl.vo.loginVo.LoginResponseVo;
import com.jnl.vo.loginVo.LogoutResponseVo;
import com.jnl.vo.tokenVo.RefreshResponseVo;

public interface loginService {

    LoginResponseVo loginByName(String username, String password);

    RefreshResponseVo refresh(String refreshToken);

    LogoutResponseVo logout(String username, String accessToken);

}
