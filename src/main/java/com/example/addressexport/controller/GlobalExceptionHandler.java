package com.example.addressexport.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String businessException(BusinessException e) {
        log.warn("业务逻辑异常 ..", e);
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String globalException(Exception e) {
        String msg = e.getMessage();
        if (msg != null && msg.startsWith("JSON parse error")) {
            // 接口参数不合法导致
            log.info("JSON解析异常, msg={} ..", e.getMessage());
        } else {
            log.warn("程序异常 ..", e);
        }
        return e.toString();
    }
}
