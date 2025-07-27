package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("permission")
public class Permission {
    private int id;

    private int uid;

    private int stationId;

}
