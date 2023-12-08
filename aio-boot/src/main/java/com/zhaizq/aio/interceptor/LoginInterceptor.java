package com.zhaizq.aio.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.zhaizq.aio.common.BusinessException;
import com.zhaizq.aio.common.CacheMap;
import com.zhaizq.aio.common.annotation.Uncheck;
import com.zhaizq.aio.common.utils.DigestUtil;
import com.zhaizq.aio.config.JsonParamResolver;
import com.zhaizq.aio.system.mapper.entity.SystemUser;
import com.zhaizq.aio.system.service.SystemLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private SystemLoginService systemLoginService;

    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Uncheck uncheck = handlerMethod.getMethodAnnotation(Uncheck.class);
        uncheck = uncheck != null ? uncheck : handlerMethod.getBeanType().getAnnotation(Uncheck.class);

        if (uncheck != null && !uncheck.verify())
            return true;

        SystemUser loginUser = systemLoginService.getLoginUser();
        this.verify(loginUser, request);

        if (uncheck == null && loginUser.getId() == null)
            throw new BusinessException(401, "未登录");

        return true;
    }

    // 请求校验
    private void verify(SystemUser loginUser, HttpServletRequest request) throws IOException {
        JSONObject jsonObject = JsonParamResolver.parseRequestJson(request);
        String uuid = jsonObject.getString("_uuid"); // 防重放
        long timestamp = jsonObject.getLongValue("_timestamp"); // 防过期
        String token = jsonObject.getString("_token"); // 防伪装

        if (!Objects.equals(token, loginUser.getToken()))
            throw new BusinessException("令牌不合法");

        if (Math.abs(System.currentTimeMillis() - timestamp) > Duration.ofMinutes(5).toMillis())
            throw new BusinessException("过期的请求");

        Object exists = CacheMap.DEFAULT.get("APP_REQUEST:UNIQUE-" + uuid);
        CacheMap.DEFAULT.put("APP_REQUEST:UNIQUE-" + uuid, Byte.BYTES, Duration.ofMinutes(10).toMillis());
        if (exists != null)
            throw new BusinessException("重复的请求");

        // 签名校验
        String body = JsonParamResolver.parseRequestData(request);
        String strData = loginUser.getSecret() + body + loginUser.getSecret();
        String mySign = DigestUtil.sha256AsHex(strData);

        String sign = request.getHeader("X-Sign");
        if (!Objects.equals(mySign, sign)) {
            log.info("mySign: {}, theySign: {}", mySign, sign);
            throw new BusinessException("签名校验失败");
        }
    }
}