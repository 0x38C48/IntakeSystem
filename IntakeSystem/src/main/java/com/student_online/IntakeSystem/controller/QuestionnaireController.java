package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Questionnaire;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.DepartmentService;
import com.student_online.IntakeSystem.service.PermissionService;
import com.student_online.IntakeSystem.service.QuestionService;
import com.student_online.IntakeSystem.service.QuestionnaireService;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/edit")//编辑部门问卷基本信息
    public ResponseEntity<Result> editQuestionnaire(@RequestParam int departmentId, @RequestBody Questionnaire questionnaire) {
        String uid = (String) request.getAttribute("uid");
        Department department= (Department) Objects.requireNonNull(departmentService.getDepartmentById(departmentId).getBody()).getData();
        if(permissionService.isPermitted(department.getStationId(),Integer.parseInt(uid))){
            return questionnaireService.saveOrUpdateQuestionnaire(questionnaire,departmentId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/edit/questions")//编辑部门问卷题目
    public ResponseEntity<Result> editQuestions(@RequestBody List<QuestionVo> questions,@RequestParam int departmentId) {
        String uid = (String) request.getAttribute("uid");
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId,Integer.parseInt(uid))){
            return questionService.saveQuestion(questions);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/delete")//删除部门问卷
    public ResponseEntity<Result> deleteQuestionnaire(@RequestParam int departmentId) {
        String uid = (String) request.getAttribute("uid");
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId,Integer.parseInt(uid))){
            return questionnaireService.deleteQuestionnaireById(departmentId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @PostMapping("/publish")
    public ResponseEntity<Result> publishQuestionnaire(@RequestParam int questionnaireId) {
        String uid = (String) request.getAttribute("uid");
        Questionnaire questionnaire= (Questionnaire) Objects.requireNonNull(questionnaireService.getQuestionnaireById(questionnaireId).getBody()).getData();
        Integer departmentId=questionnaire.getDepartmentId();
        int stationId=departmentService.getStationId(departmentId);
        if(permissionService.isPermitted(stationId,Integer.parseInt(uid))){
            return questionnaireService.publishQuestionnaire(questionnaireId);
        }else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    @GetMapping("/view/status")
    public ResponseEntity<Result> getQuestionnaireByStatus(@RequestParam int status) {
        return questionnaireService.getQuestionnairesByStatus(status);
    }
}
