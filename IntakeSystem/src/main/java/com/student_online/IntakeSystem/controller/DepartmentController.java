package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.DepartmentService;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/create")
    public ResponseEntity<Result> createDepartment(@RequestBody Department department) {//不要传入stationId这个参数，这个是和station关联，自动生成的
        String uid = (String) request.getAttribute("uid");
        int pid = department.getPId();
        if(permissionService.isPermitted(pid, Integer.parseInt(uid)) )return departmentService.createDepartment(department);
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/edit")
    public ResponseEntity<Result> editDepartment(@RequestBody Department department) {
        String uid = (String) request.getAttribute("uid");
        int pid = department.getPId();
        if(permissionService.isPermitted(pid, Integer.parseInt(uid)) )return departmentService.updateDepartment(department);
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @DeleteMapping("/del")
    public ResponseEntity<Result> deleteDepartment(@RequestBody Department department) {
        String uid = (String) request.getAttribute("uid");
        int departmentId=department.getId();
        int pid = department.getPId();
        if(permissionService.isPermitted(pid, Integer.parseInt(uid)) )return departmentService.deleteDepartment(departmentId);
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view")
    public ResponseEntity<Result> view(@RequestParam int id) {
        return departmentService.getDepartmentById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Result> search(@RequestParam String name) {
        return departmentService.getDepartmentByName(name);
    }
}
