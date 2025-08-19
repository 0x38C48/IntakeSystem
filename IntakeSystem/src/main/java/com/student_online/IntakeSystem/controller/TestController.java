package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public Result test() {
        return Result.success(1,"success");
    }
}
