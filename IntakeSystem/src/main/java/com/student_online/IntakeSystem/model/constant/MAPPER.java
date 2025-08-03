package com.student_online.IntakeSystem.model.constant;

import com.student_online.IntakeSystem.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MAPPER {
    
    @PostConstruct
    public void init(){
        user = userMapper;
    }
    
    @Autowired
    private UserMapper userMapper;
    public static UserMapper user;
}
