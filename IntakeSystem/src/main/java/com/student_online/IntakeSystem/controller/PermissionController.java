package com.student_online.IntakeSystem.controller;


import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Permission;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.StationService;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
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
    public ResponseEntity<Result> setPermission(@RequestParam int stationId, @RequestParam String username) {//不要传入stationId这个参数，这个是和station关联，自动生成的
        if(!MAPPER.user.isUsernameExists(username)){
            return ResponseUtil.build(Result.error(CommonErr.NO_DATA));
        }
        
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        Permission permission = new Permission();
        permission.setUid(Integer.parseInt(uid));
        permission.setStationId(stationId);
        String executor = ThreadLocalUtil.get().studentNumber;
        executor = MAPPER.user.getUserIdByUsername(executor) + "";
        return permissionService.createPermission(permission,Integer.parseInt(executor));
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
    public ResponseEntity<Result> showPermissionByUserId(@RequestParam(required = false) String username) {
        if(username == null || username.isEmpty()){
            username = ThreadLocalUtil.get().studentNumber;
        }
        if(!MAPPER.user.isUsernameExists(username)){
            return ResponseUtil.build(Result.error(CommonErr.NO_DATA));
        }
        int uid = MAPPER.user.getUserIdByUsername(username);
        return permissionService.getPermissionByUid(uid);
    }
}
