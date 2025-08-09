package com.student_online.IntakeSystem.model.vo;

import com.student_online.IntakeSystem.model.po.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerVo {
    private int questionnaireId;

    private List<Answer> answerList;
}
