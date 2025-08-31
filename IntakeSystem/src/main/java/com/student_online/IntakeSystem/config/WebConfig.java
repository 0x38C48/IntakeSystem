package com.student_online.IntakeSystem.config;

import com.student_online.IntakeSystem.interceptor.MyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * @author Erocatss
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private MyInterceptor myInterceptor;
    
    @Value("${avatar.path.upload}")
    private String avatarPathUpload;
    
    @Value("${avatar.path.access}")
    private String avatarPathAccess;
    
    @Value("${depart.path.upload}")
    private String departPathUpload;
    
    @Value("${depart.path.access}")
    private String departPathAccess;
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/login/cas",
                        "/avatar/**",
                        "/depart/**",
                        "/test"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler( avatarPathAccess + "**")
                .addResourceLocations("file:///"+avatarPathUpload);
        
        registry.addResourceHandler( departPathAccess + "**")
                .addResourceLocations("file:///"+departPathUpload);
    }
}
