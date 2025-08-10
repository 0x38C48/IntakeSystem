package com.student_online.IntakeSystem.model.vo;


import com.student_online.IntakeSystem.model.po.Option;
import com.student_online.IntakeSystem.model.po.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVo extends Question {


    private List<Option> option;


    private Integer type;//题目类型

    @Override
    public String toString() {
        return "[QuestionDto{" +
                "问题ID:" + super.getId() +
                ", 问卷ID:" + super.getQuestionnaireId() +
                ", 问题类型:" + super.getType() +
                ", 问题内容:'" + super.getContent() + '\'' +
                ", 问题序号:'" + super.getSort() + '\'' +
                ", Option=" + option +
                "}]\n";
    }
}
