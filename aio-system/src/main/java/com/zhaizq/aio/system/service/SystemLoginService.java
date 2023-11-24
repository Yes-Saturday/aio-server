package com.zhaizq.aio.system.service;

import com.zhaizq.aio.common.CacheMap;
import com.zhaizq.aio.common.utils.StringUtil;
import com.zhaizq.aio.system.mapper.entity.SystemUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
public class SystemLoginService {
    public final static String LOGIN_TOKEN_KEY = "X-Token";

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private SystemUserService systemUserService;

    public SystemUser getLoginUser() {
        SystemUser sysUser = (SystemUser) request.getAttribute("LOGIN_USER");
        if (sysUser != null) return sysUser;

        String token = this.getToken();
        if (StringUtil.isEmpty(token)) return null;

        sysUser = (SystemUser) CacheMap.DEFAULT.get("APP_LOGIN:TOKEN-" + token, Duration.ofDays(7).toMillis());
        request.setAttribute("LOGIN_USER", sysUser);
        return sysUser;
    }

    public SystemUser login(int userid) {
        SystemUser sysUser = systemUserService.getById(userid);
        if (sysUser == null) throw new RuntimeException("userid is not exists!!!");

        sysUser.setPassword(null);
        sysUser.setSalt(null);
        sysUser.setToken(this.getToken());
        sysUser.setSecret(UUID.randomUUID().toString());

        CacheMap.DEFAULT.put("APP_LOGIN:TOKEN-" + sysUser.getToken(), sysUser, Duration.ofDays(7).toMillis());
        return sysUser;
    }

    public void logout() {
        CacheMap.DEFAULT.remove("APP_LOGIN:TOKEN-" + this.getToken());
    }

    private String getToken() {
        String token = (String) request.getAttribute(LOGIN_TOKEN_KEY);
        if (token != null) return token;

        try {
            token = request.getHeader(LOGIN_TOKEN_KEY);
            if (token != null) return token;

            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                Cookie cookie = Arrays.stream(cookies).filter(v -> LOGIN_TOKEN_KEY.equals(v.getName())).findAny().orElse(null);
                if (cookie != null) return token = cookie.getValue();
            }

            token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(LOGIN_TOKEN_KEY, token);
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return token;
        } finally {
            request.setAttribute(LOGIN_TOKEN_KEY, token);
        }
    }
}
