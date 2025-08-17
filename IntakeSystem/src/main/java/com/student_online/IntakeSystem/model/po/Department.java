package com.student_online.IntakeSystem.model.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("Department")
public class Department {
    private Integer id;

    private String name;

    private Integer stationId;

    @JsonProperty("pId")
    private Integer pId;

    private String description;

    private String image;


}
