package com.student_online.IntakeSystem.config.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@WebFilter("/*")
public class IPCounterFilter implements Filter {
    
    //private static final ThreadLocal<Boolean> logPrinted = new ThreadLocal<>();
    
    // 存储IP地址和对应的访问次数记录
    private static final Map<String, SlidingWindowCounter> IP_COUNTERS = new ConcurrentHashMap<>();
    // 规定时间窗口内允许的最大访问次数
    private static final int MAX_REQUESTS_PER_WINDOW = 150;
    // 规定时间窗口大小，单位为毫秒
    private static final long WINDOW_SIZE = 60 * 1000; // 1分钟
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String clientIP = getClientIP(httpRequest);
        // 测试
        //if (logPrinted.get() == null) {
//            System.out.println("本次请求的方法:" + httpRequest.getMethod());
//            System.out.println("clientIp get!!! " + clientIP);
        //logPrinted.set(true);
        //}
        
        SlidingWindowCounter counter = IP_COUNTERS.computeIfAbsent(clientIP, k -> new SlidingWindowCounter(WINDOW_SIZE));
        if (counter.getRequestCount() < MAX_REQUESTS_PER_WINDOW) {
            counter.increment();
            chain.doFilter(request, response);
        } else {
            counter.increment();
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "IP killed."); //Error 429 (Too Many Requests)
        }
    }
    
    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("Proxy-Client-IP");
        }
        if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getRemoteAddr();
        }
        return clientIP;
    }
    
    private static class SlidingWindowCounter {
        private final long windowSize;
        private final LinkedBlockingDeque<Long> timestamps;
        private int count;
        
        public SlidingWindowCounter(long windowSize) {
            this.windowSize = windowSize;
            this.timestamps = new LinkedBlockingDeque<>(MAX_REQUESTS_PER_WINDOW);
            this.count = 0;
        }
        
        public void increment() {
            long currentTime = System.currentTimeMillis();
            // 移除超出时间窗口的请求记录
            while (!timestamps.isEmpty() && timestamps.peekFirst() < currentTime - windowSize) {
                timestamps.removeFirst();
                count--;
            }
            // 增加当前请求的记录
            timestamps.addLast(currentTime);
            count++;
        }
        
        public int getRequestCount() {
            return count;
        }
    }
}
