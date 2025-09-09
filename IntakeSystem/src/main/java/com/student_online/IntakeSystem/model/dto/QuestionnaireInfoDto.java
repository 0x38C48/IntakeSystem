package com.student_online.IntakeSystem.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionnaireInfoDto {
    private String username;
    private String name;
    private LocalDateTime updateTime;
    private List<String> answerContent;
}
