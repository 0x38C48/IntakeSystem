package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("answer")
public class Answer {
    private Integer id;

    private String answerContent;

    private Integer questionId;

    private Integer finishId;

    private Integer optionId;
}
