package com.student_online.IntakeSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OriginConfig {
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration cors = new CorsConfiguration();
//        //允许所有请求源
//        cors.addAllowedOriginPattern("*");
//        //允许所有请求头
//        cors.addAllowedHeader("*");
//        //允许所有HTTP方法
//        cors.addAllowedMethod("*");
//        //允许跨域发送cookie
//        cors.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**",cors);  //封装
//        return new CorsFilter(source);
//    }
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
