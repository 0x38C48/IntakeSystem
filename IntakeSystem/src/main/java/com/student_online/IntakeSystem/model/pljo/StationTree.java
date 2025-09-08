package com.student_online.IntakeSystem.model.pljo;

import com.student_online.IntakeSystem.model.po.Station;
import lombok.Data;

import java.util.List;

@Data
public class StationTree {
    private Integer id;
    
    private String name;
    
    private String description;
    
    private Integer isDepartment;
    
    private boolean permitted;
    
    private List<StationTree> children;
    
    public StationTree(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.description = station.getDescription();
        this.isDepartment = station.getIsDepartment();
    }
}
