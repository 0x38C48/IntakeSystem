package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.StationService;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/station")
public class StationController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StationService stationService;
    @Autowired
    private PermissionService permissionService;

    @PostMapping("/create")
    public ResponseEntity<Result> createStation(@RequestBody Station station) {
        String uid = (String) request.getAttribute("uid");
        int pid = station.getPId();
        if(permissionService.isPermitted(pid, Integer.parseInt(uid)) )return stationService.createStation(station);
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/edit")
    public ResponseEntity<Result> editStation(@RequestBody Station station) {
        String uid = (String) request.getAttribute("uid");
        int pid = station.getPId();
        if(permissionService.isPermitted(pid, Integer.parseInt(uid)) )return stationService.updateStation(station);
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @DeleteMapping("/del")
    public ResponseEntity<Result> deleteStation(@RequestBody Station station) {
        String uid = (String) request.getAttribute("uid");
        int stationId=station.getId();
        int pid = station.getPId();
        if(permissionService.isPermitted(pid, Integer.parseInt(uid)) )return stationService.deleteStation(stationId);
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view")
    public ResponseEntity<Result> view(@RequestParam int id) {
        return stationService.getStationById(id);
    }

    @GetMapping("/unfold")
    public ResponseEntity<Result> unfold(@RequestParam int id) {
        return stationService.getStationByParentId(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Result> search(@RequestParam String name) {
        return stationService.getStationByName(name);
    }
}
