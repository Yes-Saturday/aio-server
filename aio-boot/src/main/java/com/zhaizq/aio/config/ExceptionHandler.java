package com.zhaizq.aio.config;

import com.zhaizq.aio.common.BusinessException;
import com.zhaizq.aio.system.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler extends BaseController {
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public Result<Void> BusinessException(BusinessException e) {
        log.warn("业务异常, code: {}, message: {}", e.getCode(), e.getMessage());
        return new Result<>(e.getCode(), null, e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Result<Void> Exception(Exception e) {
        log.error("系统内部错误", e);
        return new Result<>(500, null, "系统内部错误[" + MDC.get("trace") + "]");
    }
}