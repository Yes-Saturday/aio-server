package com.zhaizq.aio.system.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class BaseController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    protected Result<Void> success() {
        return new Result<>(200, null, null);
    }

    protected <T> Result<T> success(T data) {
        return new Result<>(200, data, null);
    }

    @Getter
    public static class Results<T> extends Result<List<T>> {
        private final long total;

        public Results(int code, List<T> data, long total, String msg) {
            super(code, data, msg);
            this.total = total;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Result<T> {
        private final int code;
        private final T data;
        private final String msg;
    }
}
