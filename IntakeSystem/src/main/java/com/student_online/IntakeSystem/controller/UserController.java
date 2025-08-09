package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.STATIC;
import com.student_online.IntakeSystem.model.dto.UserDto;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.UserService;
import com.student_online.IntakeSystem.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @PostMapping("/login/cas")
    public Result loginCas(@RequestParam @NotNull String studentNumber, @RequestParam @NotNull String password, @RequestParam(required = false) String captcha,
                           @RequestParam @NotNull String fingerprint){
        return userService.loginCas(studentNumber, password, captcha, fingerprint);
    }
    
    @PostMapping("/login")
    public Result login(@RequestParam @NotNull String studentNumber, @RequestParam @NotNull String password){
        return userService.login(studentNumber, password);
    }
    
    @PostMapping("/update/password")
    public Result updatePassword(@RequestParam(required = false) String oldPassword, @RequestParam @NotNull String newPassword){
        return userService.updatePassword(oldPassword, newPassword);
    }
    
    @PostMapping("/upload/avatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file){
        try {
            return userService.uploadAvatar(file);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(500, "上传失败");
        }
    }
    
    @GetMapping("/info")
    public Result getUserInfo(@RequestParam(required = false) String username){
        return userService.getUserInfo(username);
    }
    
    @PutMapping("/update/info")
    public Result updateUserInfo(@RequestBody UserDto userDto){
        return userService.updateUserInfo(userDto);
    }
}
