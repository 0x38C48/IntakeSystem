package com.student_online.IntakeSystem.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Integer id;

    private String content;

    private Integer questionnaireId;

    private Integer sort;

    private Integer type;// 1/2/3 单选 多选 简答
}
