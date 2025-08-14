package com.student_online.IntakeSystem.utils;


import com.student_online.IntakeSystem.mapper.PermissionMapper;
import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.po.Station;
import jakarta.annotation.Resource;

/**
 * 这个类负责检查用户权限
 */
public class PermissionUtil {
    @Resource
    private static PermissionMapper permissionMapper;
    
    @Resource
    private static StationMapper stationMapper;
    
    public static boolean check(int uid, Station station){
        if(permissionMapper.isExists(uid,station.getId())){
            return true;
        }
        if(station.getPId() == 0){
            return false;
        }
        return check(uid, stationMapper.getStationById(station.getPId()));
    }

}
