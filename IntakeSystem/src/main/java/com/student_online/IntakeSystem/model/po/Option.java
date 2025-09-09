package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("option")
public class Option {
    private Integer optionId;

    private Integer questionId;

    private String optionContent;

    private Integer optionSort;
}
