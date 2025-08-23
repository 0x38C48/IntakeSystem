package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.vo.Result;
import org.springframework.stereotype.Service;

@Service
public class ScreenService {
    public Result getUsers(String name, String studentNumber, String gender, String college, String major, String station, String department, String tag, String order, Integer page, Integer size) {
        if(order == null || order.isEmpty()){
            order = "ASC";
        } else{
            order = "DESC";
        }
        if(page == null || page < 1 || size == null || size < 1){
            page = 1;
            size = 20;
        }
        if(name != null && name.isEmpty()) {
            name = "%" + name + "%";
        }
        if(studentNumber != null && studentNumber.isEmpty()) {
            studentNumber = "%" + studentNumber + "%";
        }
        if(tag != null && tag.isEmpty()) {
            tag = "%" + tag + "%";
        }
        
        if(name == null || name.isEmpty()){
            name = "%";
        }
        if(studentNumber == null || studentNumber.isEmpty()){
            studentNumber = "%";
        }
        if(gender == null || gender.isEmpty()){
            gender = "%";
        }
        if(college == null || college.isEmpty()){
            college = "%";
        }
        if(major == null || major.isEmpty()){
            major = "%";
        }
        if(station == null || station.isEmpty()){
            station = "%";
        }
        if(department == null || department.isEmpty()) {
            department = "%";
        }
        if(tag == null || tag.isEmpty()){
            tag = "%";
        }
        
        return Result.success(MAPPER.screen.get(name, studentNumber, gender, college, major, station, department, tag, order, page*size, size),"获取成功");
    }
}
