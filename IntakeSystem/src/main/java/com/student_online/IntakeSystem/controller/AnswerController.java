package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.mapper.QuestionnaireMapper;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.*;
import com.student_online.IntakeSystem.model.vo.AnswerVo;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.*;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
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
    private QuestionnaireMapper questionnaireMapper;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private FinishService finishService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DepartmentService departmentService;
    
    @Resource
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
            
            Questionnaire questionnaire = (Questionnaire) questionnaireService.getQuestionnaireById(finish.getQuestionnaireId()).getBody().getData();
            questionnaire.setCollected(questionnaire.getCollected() - 1);
            
            questionnaireMapper.updateQuestionnaire(questionnaire);
            
            return finishService.deleteById(finishId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view/answers")
    public ResponseEntity<Result> getAnswerForFinish(@RequestParam int finishId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        Finish finish= (Finish) Objects.requireNonNull(finishService.listFinishById(finishId).getBody()).getData();
        
        Questionnaire questionnaire = (Questionnaire) questionnaireService.getQuestionnaireById(finish.getQuestionnaireId()).getBody().getData();
        Department department = MAPPER.department.getDepartmentById(questionnaire.getDepartmentId());
        
        if(permissionService.isPermitted(department.getStationId(), Integer.parseInt(uid)) || finish.getUid() == Integer.parseInt(uid)){
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
    public ResponseEntity<Result> getFinishByUsernameAndQuestionnaireId(@RequestParam(required = false) String username,@RequestParam int questionnaireId) {
        String executor = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(executor) + "";
        Object obj = questionnaireService.getQuestionnaireById(questionnaireId).getBody().getData();
        Questionnaire questionnaire= (Questionnaire) Objects.requireNonNull(questionnaireService.getQuestionnaireById(questionnaireId).getBody()).getData();
        int departmentId=questionnaire.getDepartmentId();
        int stationId=departmentService.getStationId(departmentId);
        
        if(username == null || username.isEmpty()){
            username = executor;
        }
        
        if(permissionService.isPermitted(stationId, Integer.parseInt(uid)) || Objects.equals(username,executor)){
            int userId=MAPPER.user.getUserIdByUsername(username);
            
            return finishService.getFinishForUserByQuestionnaireId(userId,questionnaireId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view/user")
    public ResponseEntity<Result> getFinishByUsername(@RequestParam(required = false) String username) {
        String executor = ThreadLocalUtil.get().studentNumber;
        int uid = MAPPER.user.getUserIdByUsername(executor);
        if(username == null || username.isEmpty()){
            username = executor;
        }
        int userId=MAPPER.user.getUserIdByUsername(username);
        if(uid==userId|| permissionService.isPermitted(1,uid)){
            return finishService.listFinishForUser(userId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }
}
