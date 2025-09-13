package com.stg.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class RequestLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingService.class);

    @Resource(name = "asyncExecutor")
    Executor asyncExecutor;

    @Async("asyncExecutor")
    public CompletableFuture<Void> logRequestStart(HttpServletRequest request){
        return CompletableFuture.runAsync(() ->{
            long startTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            request.setAttribute("startTime", startTime);
            logger.info("请求开始 - 请求方法：{}， 请求URL：{}， 请求参数：{}",
                    request.getMethod(),request.getRequestURL(),request.getQueryString());
        },asyncExecutor);
    }


    @Async("asyncExecutor")
    public CompletableFuture<Void> logRequestEnd(HttpServletRequest request, HttpServletResponse response,Exception ex){
        return CompletableFuture.runAsync(() ->{
            long startTime = (long)request.getAttribute("startTime");
            long endTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long executionTime = endTime - startTime;
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("请求结束 - 请求方法：").append(request.getMethod());
            logMessage.append(", 请求URL：").append(request.getRequestURL());
            logMessage.append(", 响应状态码：").append(response.getStatus());
            logMessage.append(", 执行时间：").append(executionTime).append("ms");
            if (ex != null){
                logMessage.append(", 异常信息：").append(ex.getMessage());
            }
            logger.info(logMessage.toString());
            if (ex != null){
                logger.error("请求处理过程中出现异常", ex);
            }
        },asyncExecutor);
    }



}
