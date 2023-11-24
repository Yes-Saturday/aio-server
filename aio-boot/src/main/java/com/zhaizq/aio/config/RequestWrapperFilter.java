package com.zhaizq.aio.config;

import com.zhaizq.aio.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
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
        MDC.put("trace", StringUtil.random(6));

        if (servletRequest.getContentType() == null || !servletRequest.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            long time = System.currentTimeMillis();
            log.info("[Request] uri: {}", this.buildRequestUri((HttpServletRequest) servletRequest));
            filterChain.doFilter(servletRequest, servletResponse);
            log.info("[Response] time: {}(ms)", System.currentTimeMillis() - time);
            return;
        }

        RequestWrapper request = new RequestWrapper((HttpServletRequest) servletRequest);
        ResponseWrapper response = new ResponseWrapper((HttpServletResponse) servletResponse);
        log.info("[Request] uri: {}, body: {}", this.buildRequestUri(request), new String(request.bytes).replaceAll("\r?\n", "\t"));

        long time = System.currentTimeMillis();
        filterChain.doFilter(request, response);

        String data = response.getContent();
        log.info("[Response] data: {}, time: {}(ms)", data.replaceAll("\r?\n", "\t"), System.currentTimeMillis() - time);

        servletResponse.setContentLength(-1);
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.getWriter().write(data);
    }

    private String buildRequestUri(HttpServletRequest request) {
        return request.getRequestURI() + (StringUtil.isNotEmpty(request.getQueryString()) ? "?" + request.getQueryString() : "");
    }

    public static class RequestWrapper extends HttpServletRequestWrapper {
        private final byte[] bytes;

        public RequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            bytes = readInputSteam(request);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new ServletInputStream() {
                private final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
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
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
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

    public static class ResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();
        private final PrintWriter printWriter = new PrintWriter(out);

        public ResponseWrapper(HttpServletResponse response) throws IOException {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return printWriter;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public void write(int i) throws IOException {
                    out.write(i);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }
            };
        }

        public String getContent() {
            return out.toString();
        }
    }
}