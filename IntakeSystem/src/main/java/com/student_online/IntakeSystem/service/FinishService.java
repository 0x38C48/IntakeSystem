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


    
    public ResponseEntity<Result> listFinish(Integer userId) {
        List<Finish> finishes=finishMapper.getFinishByUid(userId);
        if(finishes.isEmpty()){
            return ResponseUtil.build(Result.error(404,"未找到你完成的问卷"));
        }
        else return ResponseUtil.build(Result.success(finishes,"返回完成结果"));

    }


    
    public QuestionnaireAndQuestionVo setAnswerForQuestion(QuestionnaireAndQuestionVo questionnaireDetailedById, List<Answer> answerList) {

        // 获取问题列表
        List<QuestionVo> questions = questionnaireDetailedById.getQuestions();

        for (QuestionVo question : questions) {
            // 通过问题id查询对应选择的答案,使用JDK8新特性 stream流
            List<Answer> answers = answerList.stream()
                    .filter(answer -> question.getId()
                            .equals(answer.getQuestionId())).collect(Collectors.toList());
            // 单选题
            if (question.getType() == 1) {
                // 只需要取list第一个元素即可，因为只有一个数据，设置选择的选项id
                question.setAnswerContext(answers.get(0).getOptionId().toString());
            }
            // 客观题与单选题同理，设置选择的选项内容
            if (question.getType() == 3) {
                // 只需要取list第一个元素即可，因为只有一个数据，设置选择的选项id
                question.setAnswerContext(answers.get(0).getAnswerContent());
            }
            // 多选题，需要设置多个选择的选项，放入一个list
            if (question.getType() == 2) {
                List<Integer> longs = new ArrayList<>();
                for (Answer answer : answers) {
                    longs.add(answer.getOptionId());
                }
                // 设置多选答案
                question.setAnswersContext(longs);
            }
        }
        // 重新设置并返回
        questionnaireDetailedById.setQuestions(questions);
        return questionnaireDetailedById;
    }

    public ResponseEntity<Result> deleteByQuestionnaireId(Integer questionnaireId) {
        try{
            finishMapper.deleteFinishById(questionnaireId);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"删除失败"));
        }
    }
}
