package com.student_online.IntakeSystem.utils;

import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.pljo.StationTree;
import com.student_online.IntakeSystem.model.po.Station;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StationUtil {
    
    public static StationTree processStation(Station station){
        StationTree stationTree = new StationTree(station);
        processChildren(stationTree);
        
        return stationTree;
    }
    
    private static void processChildren(StationTree root, boolean isPermitted,int uid){
        if(!isPermitted && PermissionUtil.check(uid,root.getId())){
            isPermitted = true;
        }
        
        if(isPermitted){
            root.setPermitted(true);
        }
        
        List<Station> children = MAPPER.station.getChildren(root.getId());
        if(children.isEmpty()){
            return;
        }
        
        List<StationTree> childrenTree = new ArrayList<>();
        for(Station child : children){
            childrenTree.add(new StationTree(child));
        }
        for (StationTree child : childrenTree){
            processChildren(child,isPermitted,uid);
        }
        
        root.setChildren(childrenTree);
    }
    
    private static void processChildren(StationTree root){
        String username = ThreadLocalUtil.get().studentNumber;
        int uid = MAPPER.user.getUserIdByUsername(username);
        processChildren(root,false,uid);
    }
}
