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
import java.util.Objects;

@Service
public class OptionService  {
    @Autowired
    private OptionMapper optionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public ResponseEntity<Result>  getOptionByQuestionId(Integer questionId) {
        Question question = questionMapper.getQuestionById(questionId);
        if(question.getType()==3)return ResponseUtil.build(Result.error(400,"这个问题不是选择题"));
        else{
            List<Option> optionList = optionMapper.getOptionByQuestionId(questionId);
            if (!optionList.isEmpty()) {
                return ResponseUtil.build(Result.success(optionList, "获取成功"));
            } else return ResponseUtil.build(Result.error(404, "未找到该问题的选项"));
        }
    }

    public ResponseEntity<Result> deleteOption(Integer optionId) {
        try{
            optionMapper.deleteOptionById(optionId);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"删除失败"));
        }
    }

    public ResponseEntity<Result> updateOption(Option option) {
        try{
            Integer questionId = option.getQuestionId();
            List<Option> optionList = optionMapper.getOptionByQuestionId(questionId);
            for (Option option1 : optionList) {
                if (Objects.equals(option1.getOptionSort(), option.getOptionSort())&& !Objects.equals(option.getOptionId(), option1.getOptionId()))ResponseUtil.build(Result.error(400,"这个选项序号和已有选项冲突"));
            }
            optionMapper.updateOption(option);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"更新失败"));
        }
    }

    public ResponseEntity<Result> createOption(Option option) {
        try{
            Integer questionId = option.getQuestionId();
            List<Option> optionList = optionMapper.getOptionByQuestionId(questionId);
            for (Option option1 : optionList) {
                if (Objects.equals(option1.getOptionSort(), option.getOptionSort()))ResponseUtil.build(Result.error(400,"这个选项序号和已有选项冲突"));
            }
            optionMapper.createOption(option);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400,"创建失败"));
        }
    }


}
