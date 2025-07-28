package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.DepartmentMapper;
import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private StationMapper stationMapper;

    public ResponseEntity<Result> createDepartment(@NotNull Department department,int uid) {
        try {
            String name = department.getName();
            int pid = department.getPId();
            PermissionService permissionService = new PermissionService();
            if (permissionService.isPermitted(pid, uid)) {
                if (departmentMapper.getDepartmentByName(name) != null)
                    return ResponseUtil.build(Result.error(409, "该部门已存在"));
                else {
                    Station station = new Station();
                    station.setName(name);
                    station.setDescription(department.getDescription());
                    station.setPId(department.getPId());
                    stationMapper.createStation(station);
                    department.setStationId(station.getId());
                    departmentMapper.createDepartment(department);
                    return ResponseUtil.build(Result.ok());
                }
            } else return ResponseUtil.build(Result.error(401, "无权限"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "创建失败"));
        }

    }

    public ResponseEntity<Result> updateDepartment(@NotNull Department department,int uid) {
        try {
            int stationId = department.getStationId();
            PermissionService permissionService = new PermissionService();
            if (permissionService.isPermitted(stationId, uid)) {
                departmentMapper.updateDepartment(department);
                return ResponseUtil.build(Result.ok());
            } else return ResponseUtil.build(Result.error(401, "无权限"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "更新失败"));
        }
    }

    public ResponseEntity<Result> deleteDepartment(int departmentId,int uid) {
        try {
            PermissionService permissionService=new PermissionService();
            Department department=departmentMapper.getDepartmentById(departmentId);
            int stationId=department.getStationId();
            if(permissionService.isPermitted(stationId,uid)) {
                departmentMapper.deleteDepartmentById(departmentId);
                stationMapper.deleteStationById(stationId);
                return ResponseUtil.build(Result.ok());
            }else return ResponseUtil.build(Result.error(401, "无权限"));
        }catch (Exception e){
            return ResponseUtil.build(Result.error(404, "部门不存在"));
        }
    }

    public ResponseEntity<Result> getDepartmentById(int departmentId) {
        try {
            Department department = departmentMapper.getDepartmentById(departmentId);
            if (department == null) return ResponseUtil.build(Result.error(404, "未找到该部门"));
            else return ResponseUtil.build(Result.success(department, "返回部门"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getDepartmentByName(@NotNull String name) {
        try {
            Department department = departmentMapper.getDepartmentByName(name);
            if (department == null) return ResponseUtil.build(Result.error(404, "未找到该部门"));
            else return ResponseUtil.build(Result.success(department, "返回部门"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getParentStation(@NotNull Department department) {//返回部门的上一级模块
        try {
            int pId = department.getPId();
            Station result = stationMapper.getStationById(pId);
            if (result == null) return ResponseUtil.build(Result.error(404, "未找到它的上级模块"));
            else return ResponseUtil.build(Result.success(result, "返回部门的上级模块"));
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }
}
