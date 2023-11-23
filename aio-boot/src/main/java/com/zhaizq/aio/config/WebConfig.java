package com.zhaizq.aio.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * jackson 配置
     *  - LocalDateTime 反序列化格式
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(@Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}") String pattern) {
        return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern)));
    }

    // Spring 跨域处理器
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").maxAge(3600).allowedOrigins("*");
    }

    // Spring 拦截器
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
//        registry.addInterceptor(appLoginInterceptor).addPathPatterns("/app/**");
    }

    // Spring 参数转换器
    public void addFormatters(@NonNull FormatterRegistry registry) {
//        registry.addConverterFactory(new EnumConverterFactory());
    }

    // Spring 视图控制器
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
//        registry.addRedirectViewController("/", "/web");
    }

    // Spring 静态资源映射
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("file:C:/Users/zhaizhuoqun/Desktop/dist/build/h5/");
    }

    // Spring 参数转换器
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new JsonParamResolver());
    }
}