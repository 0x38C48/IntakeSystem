package com.student_online.IntakeSystem.model.po;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Error {
    private int id;
    private String username;
    private LocalDateTime createTime;
    private String _interface;
    private String content;
}
