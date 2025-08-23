package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.ScreenService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/screen")
public class ScreenController {
    @Resource
    private ScreenService screenService;
    
    //姓名、学号、性别、学院、专业、模块、部门、标记的tag字段正序、倒序查询
    
    @GetMapping("/")
    public Result getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String studentNumber,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer stationId
    ){
        return screenService.getUsers(name, studentNumber, gender, college, major, departmentId, tag, order,orderBy,stationId , page, size);
    }
}
