package com.student_online.IntakeSystem.interceptor;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    
    private final byte[] cachedBody;
    
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        // 读取原始请求体并缓存
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = this.toByteArray(requestInputStream);
    }
    
    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public ServletInputStream getInputStream() {
        // 返回包含缓存数据的输入流
        return new CachedServletInputStream(this.cachedBody);
    }
    
    @Override
    public BufferedReader getReader() {
        // 提供字符流访问方式
        return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
    }
    
    // 获取缓存的请求体字符串
    public String getCachedBody() {
        String body = new String(this.cachedBody, StandardCharsets.UTF_8);
        body = body.replaceAll("(?<=(p|P)assword=)[^&]*", "******");
        
        
        return body;
    }
    
    // 内部类：包装输入流
    private static class CachedServletInputStream extends ServletInputStream {
        
        private final ByteArrayInputStream byteArrayInputStream;
        
        public CachedServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }
        
        @Override
        public int read() {
            return this.byteArrayInputStream.read();
        }
        
        @Override
        public boolean isFinished() {
            return this.byteArrayInputStream.available() == 0;
        }
        
        @Override
        public boolean isReady() {
            return true;
        }
        
        @Override
        public void setReadListener(ReadListener readListener) {
            // 无需实现
        }
    }
}

