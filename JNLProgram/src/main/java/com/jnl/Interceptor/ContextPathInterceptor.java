package com.jnl.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ContextPathInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ContextPathInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //logger.info("请求进入拦截器，原始URI：{}",request.getRequestURI());
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        //logger.info("请求进入拦截器，原始contextPath:{}",contextPath);
        //logger.info("请求进入拦截器，原始requestURI:{}",requestURI);

        //logger.info("请求进入拦截器，原始！requestURI.startWiths(contextPath):{}",!requestURI.startsWith(contextPath));
        //如果请求URI没有包含上下文路径，则添加上
        if (!requestURI.startsWith(contextPath)){
            String newURI = contextPath + requestURI;
            response.sendRedirect(newURI);
            //logger.info("添加上下文路径后，新的URI：{}",newURI);
            return false;
        }

        return true;
    }
}
