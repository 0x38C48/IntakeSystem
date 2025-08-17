package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@AllArgsConstructor
@TableName("tag")
public class Tag {
    private int id;
    
    private int uId;
    
    private String value;
    
    private int departId;
    
    private String createTime;
}
