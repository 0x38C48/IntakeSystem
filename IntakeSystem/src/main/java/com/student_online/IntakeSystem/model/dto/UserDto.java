package com.student_online.IntakeSystem.model.dto;

import com.student_online.IntakeSystem.model.po.User;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class UserDto {
    private String email;
    
    private String qq;
    
    private String profile;
    
    @Nullable
    private String username;
    
//    public UserDto(User user){
//        this.email = user.getEmail();
//        this.qq = user.getQq();
//        this.profile = user.getProfile();
//    }
}
