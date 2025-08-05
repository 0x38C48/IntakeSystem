package com.student_online.IntakeSystem.model.dto;

import com.student_online.IntakeSystem.model.po.User;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class UserDto {
    private String username;
    
    private String gender;
    
    private String depart;
    
    private String major;
    
    private String name;
    
    private String email;
    
    private int type;//0/1/2/3分别表示普通成员、部门管理员、模块管理员、顶层管理员
    
    private String avatar;
    
    public UserDto(User user){
        this.username = user.getUsername();
        this.gender = user.getGender();
        this.depart = user.getDepart();
        this.major = user.getMajor();
        this.name = user.getName();
        this.email = user.getEmail();
        this.type = user.getType();
        this.avatar = user.getAvatar();
    }
}
