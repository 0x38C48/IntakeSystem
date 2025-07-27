package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("station")
public class Station {
    private int id;

    private String name;

    private int pId;

    private String description;
}
