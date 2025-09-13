package com.stg.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private static final String ACCESS_SECRET_KEY = "zheshiwozijixiedestgshenfenmiyaoyidingyaochaoguoguidingchangdu";
    private static final String REFRESH_SECRET_KEY = "zheshiwozijixiedestgshuaxinmiyaoyidingyaochaoguoguidingchangdu";
    private static final long ACCESS_EXPIRATION_TIME = 1000 * 60 * 60 * 2;  //2小时

    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24;//24小时

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public static String generateAccessToken(String username){
        try {
            Date now = new Date();
            SecretKey key = Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes());
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + ACCESS_EXPIRATION_TIME))
                    .signWith(key,SignatureAlgorithm.HS256)
                    .compact();
        }catch (Exception e){
            logger.error("生成token失败",e);
            return null;
        }
    }

    public static String generateRefreshToken(String username){
        try {
            Date now = new Date();
            SecretKey key = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + REFRESH_EXPIRATION_TIME))
                    .signWith(key,SignatureAlgorithm.HS256)
                    .compact();
        }catch (Exception e){
            logger.error("生成刷新token报错",e);
            return null;
        }
    }

    public static Claims validateAccessToken(String token){
        try{
            SecretKey key = Keys.hmacShaKeyFor(ACCESS_SECRET_KEY.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException eje){
            logger.info("此次身份token已过期");
            return null;
        }catch (Exception e){
            logger.error("验证身份token时服务器内部发生错误",e);
            return null;
        }
    }

    public static Claims validateRefreshToken(String token){
        try{
            SecretKey key = Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException eje){
            logger.info("此次刷新token已过期");
            return null;
        }catch (Exception e){
            logger.error("验证刷新token时服务器内部发生错误",e);
            return null;
        }
    }

    public static boolean isTokenExpired(Claims claims){
        if (claims == null){
            return true;
        }
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }


}
