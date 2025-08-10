package com.student_online.IntakeSystem.service;


import com.student_online.IntakeSystem.mapper.FinishMapper;
import com.student_online.IntakeSystem.model.po.Answer;
import com.student_online.IntakeSystem.model.po.Finish;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.QuestionnaireAndQuestionVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinishService {

    @Autowired
    private FinishMapper finishMapper;


    
    public ResponseEntity<Result> listFinishForUser(Integer userId) {
        List<Finish> finishes=finishMapper.getFinishByUid(userId);
        if(finishes.isEmpty()){
            return ResponseUtil.build(Result.error(404,"未找到你完成的问卷"));
        }
        else return ResponseUtil.build(Result.success(finishes,"返回完成结果"));

    }

    public ResponseEntity<Result> listFinishForDepartment(Integer questionnaireId) {
        List<Finish> finishes=finishMapper.getFinishByQuestionnaireId(questionnaireId);
        if(finishes.isEmpty()){
            return ResponseUtil.build(Result.error(404,"未找到完成的问卷"));
        }
        else return ResponseUtil.build(Result.success(finishes,"返回完成结果"));

    }

    public ResponseEntity<Result> listFinishById(Integer finishId) {
        Finish finish=finishMapper.getFinishById(finishId);
        if(finish==null){
            return ResponseUtil.build(Result.error(404,"未找到完成的问卷"));
        }
        else return ResponseUtil.build(Result.success(finish,"返回完成结果"));

    }


    public ResponseEntity<Result> deleteById(Integer finishId) {
        try{
            finishMapper.deleteFinishById(finishId);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"删除失败"));
        }
    }

    public ResponseEntity<Result> createFinish(Finish finish) {
        try{
            int uid=finish.getUid();
            int questionnaireId=finish.getQuestionnaireId();
            if(finishMapper.getFinishByUidAndQuestionnaireId(uid,questionnaireId)==null) {
                finishMapper.createFinish(finish);
                return ResponseUtil.build(Result.success(finish.getId(),"创建成功"));
            }
            else return ResponseUtil.build(Result.error(409,"已经存在你的作答"));
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"创建作答失败"));
        }
    }

    public ResponseEntity<Result> updateFinish(Finish finish) {
        try{
            finishMapper.updateFinish(finish);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"更新作答失败"));
        }
    }
}
