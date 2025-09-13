package com.stg.Interceptor;

import com.stg.service.impl.RequestLoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Resource
    RequestLoggingService loggingService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CompletableFuture<Void> startFuture = loggingService.logRequestStart(request);
        request.setAttribute("startFuture",startFuture);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CompletableFuture<Void> startFuture = (CompletableFuture<Void>) request.getAttribute("startFuture");
        if (startFuture != null){
            startFuture.thenRun(() ->loggingService.logRequestEnd(request, response, ex));
        }

    }
}
