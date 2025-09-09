package com.student_online.IntakeSystem.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student_online.IntakeSystem.model.po.Error;
import com.student_online.IntakeSystem.utils.JwtUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setStatus(401);
            System.out.println("token is null");
            return false;
        }
        StringBuilder sb = new StringBuilder(token);
        sb.replace(0, 7, "");
        token = sb.toString();
//        String token = request.getParameter("token");
//        System.out.println(token);
        
//        JwtUtil.CLAIMS claims = JwtUtil.getClaims(token);
        JwtUtil.CLAIMS claims = JwtUtil.getClaims(token);
//        System.out.println(claims);
        if (claims == null) {
            response.setStatus(401);
            System.out.println("token is error");
            return false;
        }
        ThreadLocalUtil.set(claims);
        Error error = new Error();
        error.setUsername(claims.studentNumber);
        
        Pattern pattern = Pattern.compile("(?<=(https://i.sdu.edu.cn/XSZX/NXXT/api|http://127.0.0.1:8081)).*");
        Matcher matcher = pattern.matcher(request.getRequestURI());
        String url = request.getRequestURI();
        if(matcher.find()) {
             url= matcher.group();
        }
        error.setUrl(url);
        
        try {
            String param = "";
            Enumeration<String> paramNames = request.getParameterNames();
            for (String paramName : Collections.list(paramNames)) {
                String PV = paramName + "=";
                if(paramName.contains("password")){
                    PV += "********";
                    param += PV + "\n";
                    continue;
                }
                
                String[] values = request.getParameterValues(paramName);
                for (String value : values) {
                    PV += value;
                }
                param += PV + "\n";
            }
            error.setParam(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            error.setBody(readRequestBody(request));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        ThreadLocalUtil.setError(error);
        return true;
    }
    
    private static String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return requestBody.toString();
    }
}
