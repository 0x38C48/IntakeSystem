package com.student_online.IntakeSystem.model.pljo;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class UserForExcel {
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("学号")
    private String username;
    @ExcelProperty("部门")
    private String department;
    @ExcelProperty("邮箱")
    private String email;
    @ExcelProperty("性别")
    private String gender;
    @ExcelProperty("专业")
    private String major;
    @ExcelProperty("学院")
    private String college;
    @ExcelProperty("标签")
    private String tag;
    
    public UserForExcel(Map<String, Object> map){
        name = (String) map.get("name");
        username = (String) map.get("username");
        department = (String) map.get("depart");
        email = map.get("email") != null ? (String) map.get("email") : "";
        gender = (String) map.get("gender");
        major = (String) map.get("major");
        college = (String) map.get("college");
        tag =  map.get("tag") != null ? (String) map.get("tag") : "无";
    }
}
