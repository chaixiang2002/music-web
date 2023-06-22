package com.example.yin.config;

import com.example.yin.interceptor.CorsInterceptor;
import com.example.yin.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/7 17:08
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public CorsInterceptor corsInterceptor() {
        return new CorsInterceptor();
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor())
                .addPathPatterns("/**").order(1);
        registry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**").order(2)
                .excludePathPatterns("/**/*.jpg", "/**/*.jpeg", "/**/*.png", "/**/*.gif", "/**/*.mp3", "/**/*.m4a");
    }

}