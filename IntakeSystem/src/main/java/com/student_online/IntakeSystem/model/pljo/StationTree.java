package com.student_online.IntakeSystem.model.pljo;

import com.student_online.IntakeSystem.model.po.Station;
import lombok.Data;

import java.util.List;

@Data
public class StationTree {
    private Integer id;
    
    private String name;
    
    private List<StationTree> children;
    
    private String description;
    
    public StationTree(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.description = station.getDescription();
    }
}
