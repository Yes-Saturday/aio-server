package com.zhaizq.aio.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * App Request Wrapper 过滤器
 */
@Slf4j
@Component
public class RequestWrapperFilter extends FilterRegistrationBean<Filter> implements Filter {
    public RequestWrapperFilter() {
        setFilter(this);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
         long time = System.currentTimeMillis();

        try {
            if (servletRequest.getContentType() != null && servletRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
                servletRequest = new RequestWrapper(request);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            if (servletRequest.getContentType() != null) {
                String uri = request.getRequestURI() + (!StringUtils.isEmpty(request.getQueryString()) ? "?" + request.getQueryString() : "");
                log.info("[Server] uri: {}, time: {}(ms)", uri, System.currentTimeMillis() - time);
            }
        }
    }

    public static class RequestWrapper extends HttpServletRequestWrapper {
        private final byte[] bytes;

        public RequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            bytes = readInputSteam(request);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return inputStream.read();
                }

                @Override
                public int available() throws IOException {
                    return inputStream.available();
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }
            };
        }

        private byte[] readInputSteam(ServletRequest request) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (InputStream inputStream = request.getInputStream()) {
                int offset;
                byte[] bytes = new byte[8 * 1024];
                while ((offset = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, offset);
                }
            }

            return out.toByteArray();
        }
    }
}