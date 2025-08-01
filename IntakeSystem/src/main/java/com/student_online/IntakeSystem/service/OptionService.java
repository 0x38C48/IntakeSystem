package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.OptionMapper;
import com.student_online.IntakeSystem.mapper.QuestionMapper;
import com.student_online.IntakeSystem.model.po.Option;
import com.student_online.IntakeSystem.model.po.Question;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService  {
    @Autowired
    private OptionMapper optionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    ResponseEntity<Result>  getOptionByQuestionId(Integer questionId) {
        Question question = questionMapper.getQuestionById(questionId);
        if(question.getType()==3)return ResponseUtil.build(Result.error(400,"这个问题不是选择题"));
        else{
            List<Option> optionList = optionMapper.getOptionByQuestionId(questionId);
            if (!optionList.isEmpty()) {
                return ResponseUtil.build(Result.success(optionList, "获取成功"));
            } else return ResponseUtil.build(Result.error(404, "未找到该问题的选项"));
        }
    }

    ResponseEntity<Result> deleteOption(Integer questionId, Integer sort) {
        try{
            optionMapper.deleteOptionByQuestionIdAndSort(questionId,sort);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"删除失败"));
        }
    }

    ResponseEntity<Result> deleteOption(Integer questionId) {
        try{
            optionMapper.deleteOptionByQuestionId(questionId);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"删除失败"));
        }
    }
}
