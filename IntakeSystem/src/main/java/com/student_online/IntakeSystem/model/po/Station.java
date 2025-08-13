package com.student_online.IntakeSystem.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;



import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("station")
public class Station {
    private Integer id;
    
    private String name;
    
    
    @JsonProperty("pId")
    private Integer pId;
    
    private String description;
    
    private Integer isDepartment;
}
