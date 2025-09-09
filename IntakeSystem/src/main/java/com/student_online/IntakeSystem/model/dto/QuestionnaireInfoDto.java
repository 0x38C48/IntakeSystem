package com.student_online.IntakeSystem.model.dto;

import com.student_online.IntakeSystem.model.po.Answer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionnaireInfoDto {
    private String username;
    private String name;
    List<Answer> answers;
    private LocalDateTime updateTime;
}
