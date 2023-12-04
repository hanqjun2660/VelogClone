package com.blog.velogclone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/dashboard");
    }

    /**
     * 외부 리소스 요청시 핸들러
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile/upload/**")       // 이런 형태의 요청이 들어오면
                .addResourceLocations("file:///C:/profile/upload/");         // 실제 로컬에 저장된 외부 경로를 알려준다.
    }
}