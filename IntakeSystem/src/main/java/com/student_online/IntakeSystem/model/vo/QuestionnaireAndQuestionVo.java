package com.student_online.IntakeSystem.model.vo;




import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;


@Data
public class QuestionnaireAndQuestionVo {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;
    
    private List<QuestionVo> questions;
}
