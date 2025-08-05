package com.student_online.IntakeSystem.utils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Enumeration;

@Component
public class SeverInfoUtil {
    
    @PostConstruct
    // 获取服务器公网IP（优先取公网地址，无公网则取内网）
    public String getPublicIpByApi() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://icanhazip.com"))
                    .build();
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("111111111111111111");
            System.out.println(response.body().trim());
            return response.body().trim(); // 返回公网IP
        } catch (Exception e) {
            throw new RuntimeException("通过API获取公网IP失败", e);
        }
    }
}
