package com.student_online.IntakeSystem.model.constant;

import com.student_online.IntakeSystem.mapper.StarMapper;
import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MAPPER {
    
    @PostConstruct
    public void init(){
        user = userMapper;
        star = starMapper;
        station = stationMapper;
    }
    
    @Autowired
    private UserMapper userMapper;
    public static UserMapper user;
    
    @Autowired
    private StarMapper starMapper;
    public static StarMapper star;
    
    @Autowired
    private StationMapper stationMapper;
    public static StationMapper station;
}
