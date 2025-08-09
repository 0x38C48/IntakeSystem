package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("station")
public class Station {
    private Integer id;

    private String name;

    private Integer pId;

    private String description;

    private Integer isDepartment;
}
