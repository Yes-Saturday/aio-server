package com.zhaizq.aio.system.controller;

import com.zhaizq.aio.system.mapper.entity.SystemUser;
import com.zhaizq.aio.system.service.SystemLoginService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public abstract class BaseController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private SystemLoginService loginService;

    protected SystemUser getLogin() {
        return loginService.getLoginUser();
    }

    protected Result<Void> success() {
        return new Result<>(200, null, null);
    }

    protected <T> Result<T> success(T data) {
        return new Result<>(200, data, null);
    }

    protected <T> Results<T> success(Collection<T> data, long total) {
        return new Results<>(200, data, total,null);
    }

    @Getter
    @AllArgsConstructor
    public static class Result<T> {
        private final int code;
        private final T data;
        private final String msg;
    }

    @Getter
    public static class Results<T> extends Result<Collection<T>> {
        private final long total;

        public Results(int code, Collection<T> data, long total, String msg) {
            super(code, data, msg);
            this.total = total;
        }
    }
}
