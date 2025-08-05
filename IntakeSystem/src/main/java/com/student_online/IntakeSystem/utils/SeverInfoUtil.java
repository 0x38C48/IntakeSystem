package com.student_online.IntakeSystem.utils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Component
public class SeverInfoUtil {
    
    @PostConstruct
    // 获取服务器公网IP（优先取公网地址，无公网则取内网）
    public String getPublicIp() {
        try {
            System.out.println("111111111111111111111111");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // 过滤回环接口和禁用的接口
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // 过滤内网地址（10.x.x.x、192.168.x.x、172.16.x.x等）
                    if (!addr.isSiteLocalAddress() && !addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        System.out.println(addr.getHostAddress());
                        return addr.getHostAddress();
                    }
                }
            }
            // 若未找到公网IP，返回内网IP（或服务器hostname）
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("获取服务器IP失败", e);
        }
    }
}
