package com.student_online.IntakeSystem.model.constant;

import com.student_online.IntakeSystem.mapper.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MAPPER {
    
    @PostConstruct
    public void init(){
        user = userMapper;
        star = starMapper;
        station = stationMapper;
        permission = permissionMapper;
        tag = tagMapper;
        screen = screenMapper;
        department = departmentMapper;
        error = errorMapper;
        questionnaire = questionnaireMapper;
        finish = finishMapper;
    }
    @Resource
    private FinishMapper finishMapper;
    public static FinishMapper finish;
    
    @Resource
    private QuestionnaireMapper questionnaireMapper;
    public static QuestionnaireMapper questionnaire;
    
    @Resource
    private ErrorMapper errorMapper;
    public static ErrorMapper error;
    
    @Resource
    private DepartmentMapper departmentMapper;
    public static DepartmentMapper department;
    
    @Resource
    private ScreenMapper screenMapper;
    public static ScreenMapper screen;
    
    @Autowired
    private UserMapper userMapper;
    public static UserMapper user;
    
    @Autowired
    private StarMapper starMapper;
    public static StarMapper star;
    
    @Autowired
    private StationMapper stationMapper;
    public static StationMapper station;
    
    @Autowired
    private PermissionMapper permissionMapper;
    public static PermissionMapper permission;
    
    @Resource
    private TagMapper tagMapper;
    public static TagMapper tag;
}
