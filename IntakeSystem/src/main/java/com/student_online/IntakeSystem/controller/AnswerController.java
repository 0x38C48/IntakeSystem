package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Answer;
import com.student_online.IntakeSystem.model.po.Finish;
import com.student_online.IntakeSystem.model.vo.AnswerVo;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.AnswerService;
import com.student_online.IntakeSystem.service.DepartmentService;
import com.student_online.IntakeSystem.service.FinishService;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private FinishService finishService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/create/finish")
    public ResponseEntity<Result> createFinish(@RequestBody Finish finish) {
        return finishService.createFinish(finish);
    }

    @PostMapping("/update/finish")
    public ResponseEntity<Result> updateFinish(@RequestBody Finish finish) {
        return finishService.updateFinish(finish);
    }

    @PostMapping("/save")
    public ResponseEntity<Result> createAnswer(@RequestBody AnswerVo answerVo,@RequestParam int finishId) {
        return answerService.saveAnswer(answerVo, finishId);
    }

    @PostMapping("/edit")
    public ResponseEntity<Result> editAnswer(@RequestBody AnswerVo answerVo,@RequestParam int finishId) {
        return answerService.editAnswer(answerVo, finishId);
    }


    @PostMapping("/delete")//删除回答结果
    public ResponseEntity<Result> deleteAnswer(@RequestParam int finishId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        Finish finish= (Finish) Objects.requireNonNull(finishService.listFinishById(finishId).getBody()).getData();
        if(Integer.parseInt(uid)==finish.getUid()){
            return finishService.deleteById(finishId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view/answers")
    public ResponseEntity<Result> getAnswerForFinish(@RequestParam int finishId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        Finish finish= (Finish) Objects.requireNonNull(finishService.listFinishById(finishId).getBody()).getData();
        if(Integer.parseInt(uid)==finish.getUid()){
            return answerService.getAnswerByFinishId(finishId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }


    @GetMapping("/view/department")
    public ResponseEntity<Result> getFinishByDepartmentId(@RequestParam int departmentId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId, Integer.parseInt(uid))){
            return finishService.listFinishForDepartment(departmentId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view/user")
    public ResponseEntity<Result> getFinishByUid(@RequestParam int userId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        if(Integer.parseInt(uid)==userId){
            return finishService.listFinishForUser(userId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }
}
