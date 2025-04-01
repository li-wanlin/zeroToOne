package com.jnl.sevice;

import com.jnl.vo.loginVo.LoginResponseVo;
import com.jnl.vo.loginVo.LogoutResponseVo;
import com.jnl.vo.tokenVo.RefreshResponseVo;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface loginService {

    LoginResponseVo loginByName(String username, String password);

    RefreshResponseVo refresh(String refreshToken);

    LogoutResponseVo logout(String username, String accessToken);

}
