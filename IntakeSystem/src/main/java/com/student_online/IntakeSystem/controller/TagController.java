package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
public class TagController {
    @Resource
    private TagService tagService;
    
    @PostMapping("/create")
    public Result create(@RequestParam String username, @RequestParam String value, @RequestParam int departId){
        return tagService.create(username, value, departId);
    }
    
    @GetMapping("/view")
    public Result view(@RequestParam String username, @RequestParam int departId){
        return tagService.view(username, departId);
    }
    
    @DeleteMapping("/delete")
    public Result delete(@RequestParam int tagId){
        return tagService.delete(tagId);
    }
}
