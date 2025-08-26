package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.pljo.StationTree;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StationService {
    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private MAPPER mAPPER;
    
    public ResponseEntity<Result> createStation(Station station,int uid) {
        try {
            String name = station.getName();
            int pid = station.getPId();
            if (stationMapper.getStationByName(name) != null)
                return ResponseUtil.build(Result.error(409, "该模块已存在"));
//            else if(station.getIsDepartment()==0)return ResponseUtil.build(Result.error(400, "无法创建部门"));
            else {

                if (permissionService.isPermitted(pid, uid)) {
                    stationMapper.createStation(station);
                    return ResponseUtil.build(Result.ok());
                } else return ResponseUtil.build(Result.error(401, "无权限"));
            }
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.build(Result.error(400, "创建失败"+e.getMessage()));
        }

    }

    public ResponseEntity<Result> updateStation(Station station,int uid) {
        try {
            int stationId = station.getId();
            if (permissionService.isPermitted(stationId, uid)) {
                stationMapper.updateStation(station);
                return ResponseUtil.build(Result.ok());
            } else return ResponseUtil.build(Result.error(401, "无权限"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "更新失败"));
        }

    }

    public ResponseEntity<Result> deleteStation( int stationId,int uid) {
        try {
            if(permissionService.isPermitted(stationId, uid )) {
                stationMapper.deleteStationById(stationId);
                return ResponseUtil.build(Result.ok());
            }
            else return ResponseUtil.build(Result.error(401,"无权限"));
        }catch (Exception e){
            return ResponseUtil.build(Result.error(404, "模块不存在"));
        }
    }

    public ResponseEntity<Result> getStationById( int stationId) {
        try {
            Station station = stationMapper.getStationById(stationId);
            if (station == null) return ResponseUtil.build(Result.error(404, "未找到该模块"));
            else return ResponseUtil.build(Result.success(station, "返回模块"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getStationByName(@NotNull String name) {
        try {
            List<Station> stations = stationMapper.getStationsByName(name);
            if (stations.isEmpty()) return ResponseUtil.build(Result.error(404, "未找到该模块"));
            else return ResponseUtil.build(Result.success(stations, "返回模块"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }

    }

    public ResponseEntity<Result> getStationByParentId( int pid) {
        try {
            String executor = ThreadLocalUtil.get().studentNumber;
            int uid = MAPPER.user.getUserIdByUsername(executor);
            
            List<Station> result = stationMapper.getStationByParentId(pid);
            
            List<Map<String, Object>> list =MapUtil.transToListMap(result);
            
            for(Map<String, Object> map: list){
                int stationId = (int) map.get("id");
                if(PermissionUtil.check(uid, MAPPER.station.getStationById(stationId))){
                    map.put("permitted", true);
                }
            }
            
            if (result.isEmpty()) return ResponseUtil.build(Result.error(404, "未找到它的子模块"));
            else return ResponseUtil.build(Result.success(list, "返回子模块"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }
    
    public Result allIn() {
        List<Station> stationRoots = stationMapper.getRoots();
        List<StationTree> stationTrees = new ArrayList<>();
        for (Station station : stationRoots) {
            StationTree root = StationUtil.processStation(station);
            stationTrees.add(root);
        }
        
        return Result.success(stationTrees, "返回整个结构");
    }
}
