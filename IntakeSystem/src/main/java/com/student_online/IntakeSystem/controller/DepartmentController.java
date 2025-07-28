package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.DepartmentService;
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

    @PostMapping("/create")
    public ResponseEntity<Result> createDepartment(@RequestBody Department department) {//不要传入stationId这个参数，这个是和station关联，自动生成的
        String uid = (String) request.getAttribute("uid");
        return departmentService.createDepartment(department,Integer.parseInt(uid));
    }

    @PostMapping("/edit")
    public ResponseEntity<Result> editDepartment(@RequestBody Department department) {
        String uid = (String) request.getAttribute("uid");
        return departmentService.updateDepartment(department,Integer.parseInt(uid));
    }

    @DeleteMapping("/del")
    public ResponseEntity<Result> deleteDepartment(@RequestParam int departmentId) {
        String uid = (String) request.getAttribute("uid");
        return departmentService.deleteDepartment(departmentId,Integer.parseInt(uid));
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
