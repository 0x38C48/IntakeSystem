package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.STATIC;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.UserService;
import com.student_online.IntakeSystem.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @PostMapping("/login/cas")
    public Result loginCas(@RequestParam String studentNumber,@RequestParam String password,@RequestParam(required = false) String captcha){
        return userService.loginCas(studentNumber, password, captcha);
    }
    
    @PostMapping("/login")
    public Result login(@RequestParam String studentNumber, @RequestParam String password){
        return userService.login(studentNumber, password);
    }
    
    @PostMapping("/update/password")
    public Result updatePassword(@RequestParam(required = false) String oldPassword, @RequestParam String newPassword){
        return userService.updatePassword(oldPassword, newPassword);
    }
}
