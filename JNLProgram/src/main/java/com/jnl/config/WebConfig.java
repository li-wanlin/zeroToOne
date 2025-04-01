package com.jnl.config;

import com.jnl.Interceptor.ContextPathInterceptor;
import com.jnl.Interceptor.JwtInterceptor;
import com.jnl.Interceptor.RequestLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig  implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Resource
    ContextPathInterceptor contextPathInterceptor;

    @Resource
    JwtInterceptor jwtInterceptor;

    @Resource
    RequestLoggingInterceptor requestLoggingInterceptor;

    public WebConfig(){
        logger.info("WebConfig is initialized.");
    }


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        logger.info("Configuring path match...");
        configurer.setUseSuffixPatternMatch(false);
        configurer.setUseTrailingSlashMatch(true);
        logger.info("Path match configuration completed");

        WebMvcConfigurer.super.configurePathMatch(configurer);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        //上下文拦截器，目前没用
/*        registry.addInterceptor(contextPathInterceptor)
                .addPathPatterns("/**");*/

        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**");

        //将token身份验证组件注册到拦截器中，开发调试时关闭
/*        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/JNLProgram/loginByName","/JNLProgram/users/refresh","/JNLProgram/logout");*/
    }
}
