package com.student_online.IntakeSystem.model.vo;




import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;


@Data
public class QuestionnaireAndQuestionVo {

    private Long questionnaireId;

    private String questionnaireTitle;

    private String questionnaireDescription;

    private LocalDateTime questionnaireStartTime;

    private LocalDateTime questionnaireEndTime;

    private Integer questionnaireStatus;
    
    private List<QuestionVo> questions;
}
