package com.jnl.config;

import com.jnl.Interceptor.ContextPathInterceptor;
import com.jnl.Interceptor.DataLoggingInterceptor;
import com.jnl.Interceptor.JwtInterceptor;
import com.jnl.Interceptor.RequestLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

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
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加默认资源处理器（用于调试）
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");


        // 映射规则：前端访问 "/display/**" 时，对应服务器本地 "/usr/local/upload/STG/Images/display/" 目录
        registry.addResourceHandler("/display/**")  // 前端访问的 URL 前缀
                .addResourceLocations("file:/usr/local/upload/Images/display/");  // 服务器本地图片目录
                //.addResourceLocations("file:D:/usr/local/upload/Images/display/");  // 服务器本地图片目录
        //logger.info("图片资源映射已配置：/STGProgram/img/** → file:D:/usr/local/upload/STG/Images/eduTrain/");
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //将token身份验证组件注册到拦截器中，开发调试时关闭
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/JNLProgram/loginByName",
                        "/JNLProgram/refresh",
                        "/JNLProgram/logout",
                        "/JNLProgram/display/**",
                        "/JNLProgram/fg/**",        // 放行 fg 目录及其所有子资源
                        "/JNLProgram/js/**",        // 放行 fg 目录及其所有子资源
                        "/JNLProgram/css/**",       // 放行 css 目录
                        "/JNLProgram/fonts/**",     // 放行 fonts 目录
                        "/JNLProgram/img/**",       // 放行 img 目录
                        "/JNLProgram/imgs/**",      // 放行 imgs 目录
                        "/JNLProgram/models/**",    // 放行 models 目录
                        "/JNLProgram/pdfs/**",      // 放行 pdfs 目录
                        "/JNLProgram/static/**",    // 放行 static 目录
                        "/JNLProgram/config.js",    // 放行特定文件
                        "/JNLProgram/favicon.ico",
                        "/JNLProgram/index.html"
                );


        //上下文拦截器，目前没用
/*        registry.addInterceptor(contextPathInterceptor)
                .addPathPatterns("/**");*/

        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**");



    }
}
