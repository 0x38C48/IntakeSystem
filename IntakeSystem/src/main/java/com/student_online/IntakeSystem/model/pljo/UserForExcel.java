package com.student_online.IntakeSystem.model.pljo;

import com.alibaba.excel.annotation.ExcelProperty;

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
    
}
