package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.PermissionMapper;
import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.po.Permission;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private StationMapper stationMapper;

    public ResponseEntity<Result> createPermission(@NotNull Permission permission,int uid) {
        try {
            int stationId = permission.getStationId();
            int pid = stationMapper.getStationById(stationId).getPId();
            if (permissionMapper.getPermissionByUidAndStationId(uid, stationId) != null)
                return ResponseUtil.build(Result.error(409, "该权限已存在"));
            else {
                if (this.isPermitted(pid, uid)) {
                    permissionMapper.createPermission(permission);
                    return ResponseUtil.build(Result.ok());
                } else return ResponseUtil.build(Result.error(401, "无权限"));
            }
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "给予权限失败"));
        }
    }

    public ResponseEntity<Result> deletePermission(int id,int uid) {
        try{
            Permission permission = permissionMapper.getPermissionById(id);
            int stationId = permission.getStationId();
            int pid = stationMapper.getStationById(stationId).getPId();
            if (this.isPermitted(pid, uid)) {
                permissionMapper.deletePermissionById(id);
                return ResponseUtil.build(Result.ok());
            }else return ResponseUtil.build(Result.error(401, "无权限"));
        }catch (Exception e){
            return ResponseUtil.build(Result.error(404, "权限不存在"));
        }
    }

    public ResponseEntity<Result> getPermissionById(int permissionId) {
        try {
            Permission permission = permissionMapper.getPermissionById(permissionId);
            if (permission == null) return ResponseUtil.build(Result.error(404, "未找到该权限"));
            else return ResponseUtil.build(Result.success(permission, "返回权限"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getPermissionByUid(int uid) {
        try {
            List<Permission> permissions = permissionMapper.getPermissionByUid(uid);
            if (permissions.isEmpty()) return ResponseUtil.build(Result.error(404, "未找到权限"));
            else return ResponseUtil.build(Result.success(permissions, "返回权限"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getPermissionByStationId(int stationId){
        try {
            List<Permission> permissions = permissionMapper.getPermissionByStationId(stationId);
            if (permissions.isEmpty()) return ResponseUtil.build(Result.error(404, "未找到权限"));
            else return ResponseUtil.build(Result.success(permissions, "返回权限"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public boolean isPermitted( int stationId,int uid) {
        try {
            List<Permission> permissions = permissionMapper.getPermissionByUid(uid);
            for (Permission permission : permissions) {
                int curStationId = stationId, topStationId = permission.getStationId();
                while (curStationId != 0) {
                    if (curStationId == topStationId) return true;
                    else curStationId = stationMapper.getStationById(curStationId).getPId();
                }
            }
            return false;
        }catch (Exception e) {
            return false;
        }
    }
}
