package com.student_online.IntakeSystem.interceptor;

import com.student_online.IntakeSystem.utils.JwtUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        StringBuilder sb = new StringBuilder(token);
        sb.replace(0, 7, "");
        token = sb.toString();
        System.out.println(token);
        if (token == null) {
            response.setStatus(401);
            System.out.println("token is null");
            return false;
        }
        JwtUtil.CLAIMS claims = JwtUtil.getClaims(token);
        System.out.println(claims);
        if (claims == null) {
            response.setStatus(401);
            System.out.println("token is error");
            return false;
        }
        ThreadLocalUtil.set(claims);
        return true;
    }
}
