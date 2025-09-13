package com.stg.service.impl;


import com.stg.entity.TokenBlacklist;
import com.stg.entity.TokenCheck;
import com.stg.entity.UserInfo;
import com.stg.service.loginService;
import com.stg.utils.JwtUtils;
import com.stg.vo.functionVo.GlobalTokenBlack;
import com.stg.vo.functionVo.GlobalTokenCheckList;
import com.stg.vo.functionVo.Meta;
import com.stg.vo.loginVo.LoginData;
import com.stg.vo.loginVo.LoginResponseVo;
import com.stg.vo.loginVo.LogoutResponseVo;
import com.stg.vo.tokenVo.RefreshDataVo;
import com.stg.vo.tokenVo.RefreshResponseVo;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class loginServiceImpl implements loginService {

    @Resource
    UserInfoServiceImpl userInfoService;

    @Resource
    TokenCheckServiceImpl tokenCheckService;

    @Resource
    TokenBlacklistServiceImpl tokenBlacklistService;


    @Resource
    GlobalTokenCheckList globalTokenCheckList;

    @Resource
    GlobalTokenBlack globalTokenBlack;



    @Override
    public LoginResponseVo loginByName(String username, String password) {
        //未加入token版本
/*        loginResponseVo loginResponseVo = new loginResponseVo();
        data data = new data();
        meta meta = new meta();
        UserInfo userInfo = userInfoService.loginByName(username, password);
        if (userInfo.getId() == null){
            meta.setMsg("账号或密码不正确");
            meta.setStatus(401);
            loginResponseVo.setData(data);
            loginResponseVo.setMeta(meta);
            return loginResponseVo;
        }
        meta.setStatus(200);
        meta.setMsg("登录成功");


        data.setId(userInfo.getId());
        data.setRid(userInfo.getRid());
        data.setUsername(userInfo.getUsername());
        data.setMobile(userInfo.getMobile());
        data.setEmail(userInfo.getEmail());
        data.setToken(userInfo.getToken());

        loginResponseVo.setMeta(meta);
        loginResponseVo.setData(data);
        return loginResponseVo;*/

        Meta meta = new Meta();
        LoginData data = new LoginData();
        LoginResponseVo loginResponseVo = new LoginResponseVo();


        //加入token版本
        UserInfo userInfo = userInfoService.loginByName(username, password);

        //用户名账号或密码错误
        if (userInfo.getId() == null){
            meta.setStatus(401);
            meta.setMsg("incorrect username or password");
            loginResponseVo.setData(data);
            loginResponseVo.setMeta(meta);
            return loginResponseVo;
        }

        String accessToken = JwtUtils.generateAccessToken(username);
        String refreshToken = JwtUtils.generateRefreshToken(username);


        data.setUsername(userInfo.getUsername());
        data.setNickname(userInfo.getReserves1());  //昵称
        data.setLv(userInfo.getLv());  //权限
        data.setAccessToken(accessToken);
        data.setRefreshToken(refreshToken);

        meta.setStatus(200);
        meta.setMsg("login success");
        loginResponseVo.setMeta(meta);
        loginResponseVo.setData(data);



        //在登录操作将token信息放入tokenCheck表中
        TokenCheck tokenCheck = new TokenCheck();
        tokenCheck.setUsername(username);
        tokenCheck.setNewAccessToken(accessToken);
        tokenCheck.setNewRefreshToken(refreshToken);
        //tokenCheck.setDelayedRefreshToken(5000);
        tokenCheck.setDelayedAccessToken(5000);
        tokenCheck.setLoginTime(new Date());

        boolean save = tokenCheckService.save(tokenCheck);
        if (save){
            globalTokenCheckList.addTokenCheck(tokenCheck);
        }
        return loginResponseVo;
    }

    @Override
    public RefreshResponseVo refresh(String refreshToken) {

        Meta meta = new Meta();
        RefreshDataVo data = new RefreshDataVo();
        RefreshResponseVo refreshResponseVo = new RefreshResponseVo();


        Claims refreshClaims = JwtUtils.validateRefreshToken(refreshToken);

        //无法解析刷新令牌，过期或伪造，提醒客户端登录
        if (refreshClaims == null || JwtUtils.isTokenExpired(refreshClaims)){

            meta.setStatus(401);
            meta.setMsg("Refresh token expired.Please log in again");

            refreshResponseVo.setMeta(meta);
            refreshResponseVo.setData(data);

            return refreshResponseVo;
        }


/*        可以解析令牌的情况
        2.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果是新token，执行刷新操作，将原本两新的值放入两老，将生成的两个token放入两新
        3.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果是老token且未超过容忍时间，将两新token返回给客户端
        4.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果是老token且超过容忍时间，返回刷新token过期
        5.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果既不是新token也不是老token，返回刷新token过期*/
        String username = refreshClaims.getSubject();

        List<TokenCheck> globalCheckList = globalTokenCheckList.getGlobalCheckList();
        List<TokenCheck> userTokenList = globalCheckList.stream()
                .filter(tokenCheck -> tokenCheck.getUsername().equals(username))
                .collect(Collectors.toList());
        //伪造用户
        if (userTokenList.size() == 0){

            meta.setStatus(401);
            meta.setMsg("Refresh token expired.Please log in again");

            refreshResponseVo.setMeta(meta);
            refreshResponseVo.setData(data);

            return refreshResponseVo;
        }

        //2.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果是新token，执行刷新操作，将原本两新的值放入两老，将生成的两个token放入两新
        List<TokenCheck> newRefreshList = userTokenList.stream()
                .filter(tokenCheck -> tokenCheck.getNewRefreshToken().equals(refreshToken))
                .collect(Collectors.toList());
        if (newRefreshList.size() > 0){
            String newAccessToken = JwtUtils.generateAccessToken(username);
            String newRefreshToken = JwtUtils.generateRefreshToken(username);

            meta.setStatus(200);
            meta.setMsg("Refresh success");

            data.setAccessToken(newAccessToken);
            data.setRefreshToken(newRefreshToken);

            refreshResponseVo.setMeta(meta);
            refreshResponseVo.setData(data);


            TokenCheck newRefresh = newRefreshList.get(0);
            newRefresh.setOldRefreshToken(newRefresh.getNewRefreshToken());
            newRefresh.setOldAccessToken(newRefresh.getNewAccessToken());
            newRefresh.setNewRefreshToken(newRefreshToken);
            newRefresh.setNewAccessToken(newAccessToken);
            //设置老刷新token在新刷新token生成的2分钟内有效
            newRefresh.setDelayedRefreshToken(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1000 * 60 * 2);
            tokenCheckService.updateById(newRefresh);
            return refreshResponseVo;
        }

        //3.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果是老token且未超过容忍时间，将两新token返回给客户端
        //4.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果是老token且超过容忍时间，返回刷新token过期
        List<TokenCheck> oldRefreshList = userTokenList.stream()
                .filter(tokenCheck -> refreshToken.equals(tokenCheck.getOldRefreshToken()))
                .collect(Collectors.toList());
        if (oldRefreshList.size() > 0){
            TokenCheck oldRefresh = oldRefreshList.get(0);
            if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > oldRefresh.getDelayedRefreshToken()){
                meta.setStatus(401);
                meta.setMsg("Refresh token expired.Please log in again");

                refreshResponseVo.setMeta(meta);
                refreshResponseVo.setData(data);

                return refreshResponseVo;
            }

            meta.setStatus(204);  //2025.04.03：返回204，容忍时间内的刷新不在返回数据
            meta.setMsg("Refresh success");

/*            data.setAccessToken(oldRefresh.getNewAccessToken());
            data.setRefreshToken(oldRefresh.getNewRefreshToken());*/

            refreshResponseVo.setMeta(meta);
            refreshResponseVo.setData(data);

            return refreshResponseVo;
        }

        //5.若刷新token可以解析，每次请求过来，使用用户信息在数据库查询进行比对。如果既不是新token也不是老token，返回刷新token过期
        meta.setStatus(401);
        meta.setMsg("Refresh token expired.Please log in again");

        refreshResponseVo.setMeta(meta);
        refreshResponseVo.setData(data);

        return refreshResponseVo;

    }

    @Override
    public LogoutResponseVo logout(String username, String accessToken) {
        List<TokenCheck> globalCheckList = globalTokenCheckList.getGlobalCheckList();
        Date now = new Date();
        List<TokenCheck> checkList = globalCheckList.stream()
                .filter(tokenCheck -> username.equals(tokenCheck.getUsername()))
                .filter(tokenCheck -> accessToken.equals(tokenCheck.getNewAccessToken()) || accessToken.equals(tokenCheck.getOldAccessToken()))
                .collect(Collectors.toList());
        if (checkList.size() > 0){
            TokenCheck tokenCheck = checkList.get(0);
            tokenCheck.setLogoutTime(now);
            tokenCheckService.updateById(tokenCheck);
        }
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setUsername(username);
        tokenBlacklist.setAccessToken(accessToken);
        tokenBlacklist.setTime(now);
        boolean save = tokenBlacklistService.save(tokenBlacklist);
        if (save){
            globalTokenBlack.addTokenBlacklist(tokenBlacklist);
        }

        Meta meta = new Meta();
        LogoutResponseVo logoutResponseVo = new LogoutResponseVo();
        meta.setStatus(200);
        meta.setMsg("Logout success");

        logoutResponseVo.setMeta(meta);
        return logoutResponseVo;
    }


}
