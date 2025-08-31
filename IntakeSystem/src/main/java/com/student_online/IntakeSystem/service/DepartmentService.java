package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.DepartmentMapper;
import com.student_online.IntakeSystem.mapper.StationMapper;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private StationMapper stationMapper;
    
    @Value("${depart.path.upload}")
    private String uploadPath;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Value("${server.domain}")
    private String serverDomain;
    
    @Value("${depart.path.access}")
    private String accessPath;
    
    @Value("${spring.profiles.active}")
    private String env;

    public ResponseEntity<Result> createDepartment(@NotNull Department department,int uid) {
        try {
            String name = department.getName();
            int pid = department.getPId();
            PermissionService permissionService = new PermissionService();
            if (permissionService.isPermitted(pid, uid)) {
                if (departmentMapper.getDepartmentByName(name) != null) {
                    return ResponseUtil.build(Result.error(409, "该部门已存在"));
                } else {
                    Station station = new Station();
                    station.setName(name);
                    station.setDescription(department.getDescription());
                    station.setPId(department.getPId());
                    station.setIsDepartment(1);
                    stationMapper.createStation(station);
                    station = stationMapper.getStationByName(name);
                    department.setStationId(station.getId());
                    departmentMapper.createDepartment(department);
                    
                    
                    return ResponseUtil.build(Result.ok());
                }
            } else {
                return ResponseUtil.build(Result.error(401, "无权限"));
            }
        }catch (Exception e) {
            e.printStackTrace();
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
            } else {
                return ResponseUtil.build(Result.error(401, "无权限"));
            }
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "更新失败"));
        }
    }
    
    @SneakyThrows
    private void uploadImg(int departId, MultipartFile file){
        if(file == null || file.isEmpty()){
            return;
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("上传文件非是图片");
        }
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        String fileName = "depart_" + departId;
        
        
        Path directoryPath = Paths.get(uploadPath);
        try (Stream<Path> stream = Files.list(directoryPath)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(fileName))
                    .forEach(path -> {
                        path.toFile().delete();
//                        System.out.println("删除文件: " + path.getFileName());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Path path = Paths.get(uploadPath);
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
        
        File dest = new File(uploadPath+ "/" + fileName + suffix);
        file.transferTo(dest);
        
        MAPPER.department.setImg(departId, "http"+("dev".equals(env) ? "" : "s") +  "://" + serverDomain  + ("dev".equals(env) ? ":" + serverPort : "") + accessPath + fileName + suffix);
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
            }else {
                return ResponseUtil.build(Result.error(401, "无权限"));
            }
        }catch (Exception e){
            return ResponseUtil.build(Result.error(404, "部门不存在"));
        }
    }

    public ResponseEntity<Result> getDepartmentById(Integer departmentId,Integer stationId) {
        try {
            Department department;
            if (departmentId != null) {
                department = departmentMapper.getDepartmentById(departmentId);
            } else {
                department = departmentMapper.getDepartmentByStationId(stationId);
            }
                if (department == null) {
                    return ResponseUtil.build(Result.error(404, "未找到该部门"));
                } else {
                    return ResponseUtil.build(Result.success(department, "返回部门"));
                }
            
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getDepartmentByName(@NotNull String name) {
        try {
            List<Department> departments = departmentMapper.getDepartmentsByName(name);
            if (departments.isEmpty()) {
                return ResponseUtil.build(Result.error(404, "未找到该部门"));
            } else {
                return ResponseUtil.build(Result.success(departments, "返回部门"));
            }
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getParentStation(@NotNull Department department) {//返回部门的上一级模块
        try {
            int pId = department.getPId();
            Station result = stationMapper.getStationById(pId);
            if (result == null) {
                return ResponseUtil.build(Result.error(404, "未找到它的上级模块"));
            } else {
                return ResponseUtil.build(Result.success(result, "返回部门的上级模块"));
            }
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public int getStationId(int departmentId) {//返回部门的上一级模块
        try {
            Department department = departmentMapper.getDepartmentById(departmentId);
            return department.getStationId();
        }catch (Exception e) {
            return 0;
        }
    }
    
    public ResponseEntity<Result> uploadImg(int id, int uid, MultipartFile file) {
        PermissionService permissionService = new PermissionService();
        Department department = departmentMapper.getDepartmentById(id);
        
        int stationId = department.getStationId();
        
        if (permissionService.isPermitted(stationId, uid)) {
            uploadImg(id, file);
            
            
            return ResponseUtil.build(Result.ok());
        } else {
            return ResponseUtil.build(Result.error(401, "无权限"));
        }
    }
}
