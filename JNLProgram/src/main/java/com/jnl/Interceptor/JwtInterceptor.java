package com.jnl.Interceptor;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jnl.entity.TokenBlacklist;
import com.jnl.entity.TokenCheck;
import com.jnl.utils.JwtUtils;
import com.jnl.vo.functionVo.Base64Vo;
import com.jnl.vo.functionVo.GlobalTokenBlack;
import com.jnl.vo.functionVo.GlobalTokenCheckList;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    GlobalTokenCheckList globalTokenCheckList;

    @Resource
    GlobalTokenBlack globalTokenBlack;

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //设置免token界面
/*        if (request.getRequestURI().endsWith("/JNLProgram/loginByName")
                || request.getRequestURI().endsWith("/JNLProgram/refresh")
                || request.getRequestURI().endsWith("/JNLProgram/logout")){
            return true;
        }*/

        String requestURI = request.getRequestURI();

        // 放行登录相关接口
        if (requestURI.endsWith("/JNLProgram/loginByName")
                || requestURI.endsWith("/JNLProgram/refresh")
                || requestURI.endsWith("/JNLProgram/logout")) {
            return true;
        }

        // 放行静态资源目录
        if (requestURI.startsWith(request.getContextPath() + "/fg/")        // 匹配 /JNLProgram/fg/
                || requestURI.startsWith(request.getContextPath() + "/css/")
                || requestURI.startsWith(request.getContextPath() + "/fonts/")
                || requestURI.startsWith(request.getContextPath() + "/js/")
                || requestURI.startsWith(request.getContextPath() + "/img/")
                || requestURI.startsWith(request.getContextPath() + "/imgs/")
                || requestURI.startsWith(request.getContextPath() + "/models/")
                || requestURI.startsWith(request.getContextPath() + "/display/")
                || requestURI.startsWith(request.getContextPath() + "/pdfs/")
                || requestURI.startsWith(request.getContextPath() + "/static/")) {
            return true;
        }

        // 放行特定文件
        if (requestURI.endsWith("/JNLProgram/config.js")
                || requestURI.endsWith("/JNLProgram/favicon.ico")
                || requestURI.endsWith("/JNLProgram/index.html")) {
            return true;
        }

        //获取在客户端存储的原始accessToken
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null || !accessToken.startsWith("Bearer ")){
            //sendErrorResponse(response,HttpStatus.FORBIDDEN,"Forbidden");

            sendErrorResponse(response,HttpStatus.PROXY_AUTHENTICATION_REQUIRED,"accessToken Required");

            logger.info("accessToken数据缺失");
            return false;
        }


        //开始token身份验证
        accessToken = accessToken.replace("Bearer ", "");
        String token = accessToken;



        //开发测试用，后期需注释掉
        if (accessToken.equals("lwl123456789")){
            return true;
        }


        //从payload中获取用户信息
        String[] parts = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(parts[1]);
        // 将字节数组转换为字符串
        String decodedString = new String(decodedBytes);
        ObjectMapper objectMapper = new ObjectMapper();
        Base64Vo base64Vo = objectMapper.readValue(decodedString, Base64Vo.class);
        //获取用户信息
        String username = base64Vo.getSub();
        Long exp = base64Vo.getExp();
        if (username == null || username == "" || exp == null || exp == 0){
            sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "accessToken Required");
            return false;
        }

        //验证黑名单，命中返回true，未命中返回false
        Boolean ifBlack = blackList(response, username, accessToken);
        if (ifBlack){
            return false;
        }

        Claims accessClaims = JwtUtils.validateAccessToken(accessToken);
        if (accessClaims == null || JwtUtils.isTokenExpired(accessClaims)){

            try {
                List<TokenCheck> globalCheckList = globalTokenCheckList.getGlobalCheckList();
                List<TokenCheck> userTokenList = globalCheckList.stream()
                        .filter(tokenCheck -> tokenCheck.getUsername().endsWith(username))
                        .collect(Collectors.toList());

                //用户信息在tokencheck表中找不到对应数据,认定为假token
                if (userTokenList.size() == 0){
                    sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "accessToken Required");
                    return false;
                }

                //概念：新token：本用户最新token；老token：最新token的上一个token
                //本次请求token为新token
                List<TokenCheck> newAccessToken = userTokenList.stream()
                        .filter(tokenCheck -> tokenCheck.getNewAccessToken().equals(token))
                        .collect(Collectors.toList());
                if (newAccessToken.size() > 0){
                    sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "accessToken Required");
                    return false;
                }

                //本次请求为老token
                List<TokenCheck> oldAccessTokenList = userTokenList.stream()
                        .filter(tokenCheck -> tokenCheck.getOldAccessToken().equals(token))
                        .collect(Collectors.toList());
                if (oldAccessTokenList.size() > 0){
                    TokenCheck oldAccessToken = oldAccessTokenList.get(0);
                    long nowTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    exp = exp * 1000 + oldAccessToken.getDelayedAccessToken();  //base64编码生成的时间戳是秒而不是毫秒

                    //老token时间超过过期时间+容忍时间，认定过期
                    if (nowTimestamp > exp){
                        sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "accessToken Required");
                        return false;
                    }


                    //老token时间未到过期时间+容忍时间，认定未过期(黑名单待做)
                    return true;

                }

                //本次请求既不是新token，也不是老token，返回过期
                sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "accessToken Required");
                return false;
            }catch (Exception e){
                logger.error("后续验证token逻辑出错",e);
            }




/*            //获取在客户端存储的刷新token
            String refreshToken = request.getHeader("Refresh-Token");
            if (refreshToken == null){
                sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Missing refresh token");
                logger.info("refreshToken缺失");
                return false;
            }

            //开始判断刷新token是否过期
            Claims refreshClaims = JwtUtils.validateRefreshToken(refreshToken);
            if (refreshClaims == null || JwtUtils.isTokenExpired(refreshClaims)){
                sendErrorResponse(response,HttpStatus.UNAUTHORIZED,"Refresh token expired.Please log in again.");
                logger.info("accessToken已过期，尽快执行刷新操作");
                return false;
            }

            //未过期，通知客户端发送刷新请求
            sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "Access token expired. Try to refresh.");
            return false;*/


            //已过期，通知客户端发送刷新请求
            sendErrorResponse(response, HttpStatus.PROXY_AUTHENTICATION_REQUIRED, "accessToken Required");
            return false;

        }

        //只有当身份token未过期时，才能继续向下进行，否则返回给客户端错误信息(黑名单代做)
        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message",message);
        errorResponse.put("status",status.value());
        response.getWriter().write(JSON.toJSONString(errorResponse));
    }

    /**
     * 没有命中返回false,命中黑名单返回true
     * @param username
     * @param accessToken
     * @return
     */
    private Boolean blackList(HttpServletResponse response,String username,String accessToken){
        try {
            List<TokenBlacklist> blackList = globalTokenBlack.getGlobalTokenBlackList();
            if (blackList.size() == 0){
                return false;
            }
            List<TokenBlacklist> tokenBlacklists = blackList.stream()
                    .filter(tokenBlacklist -> username.equals(tokenBlacklist.getUsername()) && accessToken.equals(tokenBlacklist.getAccessToken()))
                    .collect(Collectors.toList());
            if (tokenBlacklists.size() > 0){
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message","Hit the blacklist");
                response.getWriter().write(errorResponse.toString());
                return true;
            }
            return false;
        }catch (Exception e){
            logger.error("验证黑名单发生错误",e);
        }
        return false;
    }
}
