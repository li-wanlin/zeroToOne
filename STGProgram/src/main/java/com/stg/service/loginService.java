package com.stg.service;

import com.stg.vo.loginVo.LoginResponseVo;
import com.stg.vo.loginVo.LogoutResponseVo;
import com.stg.vo.tokenVo.RefreshResponseVo;

public interface loginService {

    LoginResponseVo loginByName(String username, String password);

    RefreshResponseVo refresh(String refreshToken);

    LogoutResponseVo logout(String username, String accessToken);

}
