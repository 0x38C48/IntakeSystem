package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@TableName("tag")
public class Tag {
    private int id;

    private int uid;

    private String value;

    private int departId;
    
    private LocalDateTime createTime;
}
