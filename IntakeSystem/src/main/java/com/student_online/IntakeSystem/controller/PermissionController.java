package com.student_online.IntakeSystem.controller;


import com.student_online.IntakeSystem.model.po.Permission;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.StationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/set")
    public ResponseEntity<Result> setPermission(@RequestBody Permission permission) {//不要传入stationId这个参数，这个是和station关联，自动生成的
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return permissionService.createPermission(permission,Integer.parseInt(uid));
    }

    @DeleteMapping("/del")
    public ResponseEntity<Result> delPermission(@RequestParam int permissionId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return permissionService.deletePermission(permissionId,Integer.parseInt(uid));
    }

    @GetMapping("/show/station")
    public ResponseEntity<Result> showPermission(@RequestParam int stationId) {
        return permissionService.getPermissionByStationId(stationId);
    }

    @GetMapping("/show/user")
    public ResponseEntity<Result> showPermissionByUserId(@RequestParam int userId) {
        return permissionService.getPermissionByUid(userId);
    }
}
