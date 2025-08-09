package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.service.AnswerService;
import com.student_online.IntakeSystem.service.FinishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private FinishService finishService;
}
