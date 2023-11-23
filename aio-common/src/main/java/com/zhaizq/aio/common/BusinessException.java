package com.zhaizq.aio.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code) {
        this(code, null);
    }

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}