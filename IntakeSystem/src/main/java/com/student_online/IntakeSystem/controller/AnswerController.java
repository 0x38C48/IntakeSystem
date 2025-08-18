package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Answer;
import com.student_online.IntakeSystem.model.po.Finish;
import com.student_online.IntakeSystem.model.po.Questionnaire;
import com.student_online.IntakeSystem.model.vo.AnswerVo;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.*;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
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
    private QuestionnaireService questionnaireService;

    @PostMapping("/create/finish")
    public ResponseEntity<Result> createFinish(@RequestBody Finish finish) {
        String executor = ThreadLocalUtil.get().studentNumber;
        int uid = MAPPER.user.getUserIdByUsername(executor);
        finish.setUid(uid);
        
        return finishService.createFinish(finish);
    }

    @PostMapping("/update/finish")
    public ResponseEntity<Result> updateFinish(@RequestBody Finish finish) {
        String executor = ThreadLocalUtil.get().studentNumber;
        int uid = MAPPER.user.getUserIdByUsername(executor);
        finish.setUid(uid);
        
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

    @GetMapping("/view/user_questionnaire")
    public ResponseEntity<Result> getFinishByUidAndQuestionnaireId(@RequestParam int userId,@RequestParam int questionnaireId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        Questionnaire questionnaire= (Questionnaire) Objects.requireNonNull(questionnaireService.getQuestionnaireById(questionnaireId).getBody()).getData();
        int departmentId=questionnaire.getDepartmentId();
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId, Integer.parseInt(uid))){
            return finishService.getFinishForUserByQuestionnaireId(userId,questionnaireId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view/user")
    public ResponseEntity<Result> getFinishByUsername(@RequestParam String username) {
        String executor = ThreadLocalUtil.get().studentNumber;
        int uid = MAPPER.user.getUserIdByUsername(executor);
        if(username == null || username.isEmpty()){
            return finishService.listFinishForUser(uid);
        }
        int userId=MAPPER.user.getUserIdByUsername(username);
        if(uid==userId){
            return finishService.listFinishForUser(userId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }
}
