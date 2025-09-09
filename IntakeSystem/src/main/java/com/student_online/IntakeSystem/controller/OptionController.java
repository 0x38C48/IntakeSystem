package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Option;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.*;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/option")
public class OptionController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private OptionService optionService;

    @PostMapping("/create")//创建部门问卷选项
    public ResponseEntity<Result> createOption(@RequestBody Option option, @RequestParam int departmentId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId,Integer.parseInt(uid))){
            return optionService.createOption(option);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/update")//更新部门问卷选项
    public ResponseEntity<Result> editOptions(@RequestBody Option option, @RequestParam int departmentId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId,Integer.parseInt(uid))){
            return optionService.updateOption(option);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/delete")//删除部门问卷选项
    public ResponseEntity<Result> deleteOption(@RequestParam Integer optionId,@RequestParam Integer departmentId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId,Integer.parseInt(uid))){
            return optionService.deleteOption(optionId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view")//查看问题选项
    public ResponseEntity<Result> getOptionByQuestion(@RequestParam int questionId) {
        return optionService.getOptionByQuestionId(questionId);
    }
}
