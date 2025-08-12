package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.pljo.StationTree;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.StationUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StarService {
    public Result query(){
        String username = ThreadLocalUtil.get().studentNumber;
        Integer uid = MAPPER.user.getUserIdByUsername(username);
        
        List<Station> stations = MAPPER.star.getStarByUsername(username);
        List<StationTree> trees = new ArrayList<>();
        for (Station s: stations) {
            trees.add(StationUtil.processStation(s));
        }
        return Result.success(trees, "获取成功");
    }
    
    public Result add(Integer stationId) {
        String username = ThreadLocalUtil.get().studentNumber;
        
        if(MAPPER.star.isStarred(username, stationId)){
            return Result.error(400, "已经收藏过了");
        }
        
        MAPPER.star.addStar(username, stationId);
        return Result.ok();
    }
    
    public Result delete(Integer stationId) {
        String username = ThreadLocalUtil.get().studentNumber;
        MAPPER.star.deleteStar(username, stationId);
        return Result.ok();
    }
}
