package com.student_online.IntakeSystem.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestCachingFilter implements Filter {
    
    private static final String[] EXCLUDED_URLS = {
            "/user/login",
            "/user/login/cas",
            "/avatar/**",
            "/depart/**",
            "/test"
    };
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest0 = (HttpServletRequest) request;
        String path = httpRequest0.getRequestURI();
        
        Pattern pattern = Pattern.compile("(?<=(https://i.sdu.edu.cn/XSZX/NXXT/api|http://127.0.0.1:8081)).*");
        Matcher matcher = pattern.matcher(path);
        String url = "";
        if(matcher.find()) {
            url= matcher.group();
        }
        
        boolean shouldExclude = false;
        for(String excludedUrl : EXCLUDED_URLS){
            if(excludedUrl.equals(url) || path.startsWith(excludedUrl)){
                shouldExclude = true;
                break;
            }
        }
        
        if (shouldExclude) {
            chain.doFilter(request, response);
        } else {
            
            // 仅包装HTTP请求
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                // 创建包装后的请求对象
                CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpRequest);
                // 继续过滤器链，使用包装后的请求
                chain.doFilter(cachedRequest, response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }
}
