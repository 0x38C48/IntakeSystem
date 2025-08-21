package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tag")
public class Tag {
    private int id;

    private int uid;

    private String value;

    private int departId;
    
    @TableField(value = "create_time",jdbcType = JdbcType.TIMESTAMP)
    private LocalDateTime createTime;
}
