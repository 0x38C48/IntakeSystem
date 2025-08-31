package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.StationService;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/station")
public class StationController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StationService stationService;

    @PostMapping("/create")
    public ResponseEntity<Result> createStation(@RequestBody Station station) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return stationService.createStation(station,Integer.parseInt(uid));
    }

    @PostMapping("/edit")
    public ResponseEntity<Result> editStation(@RequestBody Station station) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return stationService.updateStation(station,Integer.parseInt(uid));
    }

    @DeleteMapping("/del")
    public ResponseEntity<Result> deleteStation(@RequestParam int stationId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return stationService.deleteStation(stationId,Integer.parseInt(uid));
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

    @GetMapping("/allIn")
    public Result allIn() {
        return stationService.allIn();
    }
}
