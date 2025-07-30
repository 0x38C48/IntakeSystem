package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    private int uid;

    private String username;

    private String password;

    private String studentNum;

    private int type;//0/1/2/3分别表示普通成员、部门管理员、模块管理员、顶层管理员
}
