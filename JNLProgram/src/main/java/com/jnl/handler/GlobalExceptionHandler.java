package com.jnl.handler;

import com.jnl.vo.functionVo.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        logger.error("An error occurred",ex);
        return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Meta> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        Meta meta = new Meta();
        meta.setStatus(413); // 413表示Payload Too Large
        meta.setMsg("上传的文件大小超过限制，最大允许10MB");
        return new ResponseEntity<>(meta, HttpStatus.PAYLOAD_TOO_LARGE);
    }


}
