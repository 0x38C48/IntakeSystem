package com.student_online.IntakeSystem.model.constant;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class REDIS {
    
    @PostConstruct
    public void init(){
        r = redisTemplate;
    }
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    public static RedisTemplate<String, Object> r;
}
