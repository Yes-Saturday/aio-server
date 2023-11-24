package com.zhaizq.aio.config;

import com.alibaba.fastjson.JSONObject;
import com.zhaizq.aio.common.annotation.JsonParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonParamResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
          return parameter.hasMethodAnnotation(JsonParam.class) || parameter.hasParameterAnnotation(JsonParam.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) return null;

        JSONObject json = JsonParamResolver.parseRequestJson(request);
        if (json == null) return null;

        if (json.containsKey(parameter.getParameterName()))
            return json.getObject(parameter.getParameterName(), parameter.getGenericParameterType());

        if (!BeanUtils.isSimpleProperty(parameter.getNestedParameterType()))
            return json.toJavaObject(parameter.getGenericParameterType());

        return null;
    }

    public static JSONObject parseRequestJson(HttpServletRequest request) throws IOException {
        JSONObject json = (JSONObject) request.getAttribute("JSON_PARAM");
        if (json == null) {
            String body = JsonParamResolver.parseRequestData(request);
            request.setAttribute("JSON_PARAM", json = JSONObject.parseObject(body));
        }

        return json;
    }

    public static String parseRequestData(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType() != null ? request.getContentType() : "";
        if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } else if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                || contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return request.getParameter("strData");
        }

        log.warn("request content-type[{}] is not supported", request.getContentType());
        return null;
    }
}