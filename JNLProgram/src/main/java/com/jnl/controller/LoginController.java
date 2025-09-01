package com.jnl.controller;


import com.jnl.service.impl.loginServiceImpl;
import com.jnl.vo.functionVo.GlobalTokenCheckList;
import com.jnl.vo.functionVo.Meta;
import com.jnl.vo.loginVo.LogRequest;
import com.jnl.vo.loginVo.LoginData;
import com.jnl.vo.loginVo.LoginResponseVo;
import com.jnl.vo.loginVo.LogoutResponseVo;
import com.jnl.vo.tokenVo.RefreshDataVo;
import com.jnl.vo.tokenVo.RefreshResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class LoginController {

    @Resource
    loginServiceImpl loginService;

    @Resource
    GlobalTokenCheckList globalTokenCheckList;

    private final ReentrantLock refreshLock = new ReentrantLock();

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping ("/loginByName")
    public LoginResponseVo loginByName(@RequestBody LogRequest request){
        //未加上token版本
/*        loginResponseVo loginResponseVo = new loginResponseVo();
        data data = new data();
        meta meta = new meta();
        if (username == null || password == null){ //用来判断账号和密码格式
            meta.setMsg("账号或密码格式不对");
            meta.setStatus(400);
            loginResponseVo.setData(data);
            loginResponseVo.setMeta(meta);
            return loginResponseVo;
        }
        logger.info("此为登录功能方便打印:");
        loginResponseVo = loginService.loginByName(username,password);
        return loginResponseVo;*/

        String username = request.getUsername();
        String password = request.getPassword();


        //加上token版本
        if (username == null || password == null){
            Meta meta = new Meta();
            LoginData data = new LoginData();
            LoginResponseVo loginResponseVo = new LoginResponseVo();

            meta.setStatus(400);
            meta.setMsg("incorrect username or password");
            loginResponseVo.setData(data);
            loginResponseVo.setMeta(meta);
            return loginResponseVo;
        }
        return loginService.loginByName(username, password);
    }

    @PostMapping("/refresh")
    public RefreshResponseVo refresh(@RequestHeader("RefreshToken") String refreshToken){
        refreshLock.lock();

        Meta meta = new Meta();
        RefreshDataVo data = new RefreshDataVo();
        RefreshResponseVo refreshResponseVo = new RefreshResponseVo();

        try {
            if (refreshToken == null){

                meta.setStatus(400);
                meta.setMsg("Refresh-Token cannot be empty");

                refreshResponseVo.setMeta(meta);
                refreshResponseVo.setData(data);

                return refreshResponseVo;
            }
            return loginService.refresh(refreshToken);
        }catch (Exception e){
            logger.error("执行刷新token操作时controller出错",e);

            meta.setStatus(500);
            meta.setMsg("Server refresh operation error");

            refreshResponseVo.setMeta(meta);
            refreshResponseVo.setData(data);

            return refreshResponseVo;
        }finally{
            refreshLock.unlock();
        }
    }

    @PostMapping("/logout")
    public LogoutResponseVo logout(@RequestHeader("Authorization") String auth, @RequestBody LogRequest request){
        String username = request.getUsername();
        Meta meta = new Meta();
        LogoutResponseVo logoutResponseVo = new LogoutResponseVo();
        if (username == null || username.equals("") || auth == null || !auth.startsWith("Bearer ")){
            meta.setStatus(400);
            meta.setMsg("Username or token cannot be empty");

            logoutResponseVo.setMeta(meta);
            return logoutResponseVo;
        }
        return loginService.logout(username,auth.replace("Bearer ", ""));
    }
}
