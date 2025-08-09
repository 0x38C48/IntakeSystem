package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.StarService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/star")
@Validated
public class StarController {
    @Resource
    private StarService starService;
    
    @GetMapping("/query")
    public Result query() {
        return starService.query();
    }
    
    @PostMapping("/add")
    public Result add(@RequestParam("stationId") @Min(1) Integer stationId){
        return starService.add(stationId);
    }
    
    @DeleteMapping("/delete")
    public Result delete(@RequestParam("stationId") @Min(1) Integer stationId){
        return starService.delete(stationId);
    }
}
