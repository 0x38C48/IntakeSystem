package com.student_online.IntakeSystem.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.student_online.IntakeSystem.mapper.OptionMapper;
import com.student_online.IntakeSystem.mapper.QuestionMapper;
import com.student_online.IntakeSystem.mapper.QuestionnaireMapper;
import com.student_online.IntakeSystem.model.po.Option;
import com.student_online.IntakeSystem.model.po.Question;
import com.student_online.IntakeSystem.model.po.Questionnaire;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class QuestionService  {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @Autowired
    private OptionMapper optionMapper;



    public ResponseEntity<Result> saveQuestion(List<QuestionVo> questionsDto) {
        if (questionsDto.isEmpty()) {
            return ResponseUtil.build(Result.error(400, ""));
        }
        // 获取问卷id
        Integer questionnaireId = questionsDto.get(0).getQuestionnaireId();

        Questionnaire questionnaireById = questionnaireMapper.getQuestionnaireById(questionnaireId);
        // 判断问卷状态
        if (questionnaireById.getStatus() == 1) {
            return ResponseUtil.build(Result.error(400, "问卷已经发布无法修改"));
        }

        // 先删除问卷下的问题和选项(可能是更新操作)
        // 判断是否第一次设计问卷的保存，如果第一次无需再进行删除操作
        if (questionnaireMapper.getQuestionnaireById(questionnaireId)!= null) {
            // MySQL数据库设置了外键级联删除，选项会自动删除
            deleteQuestions(questionnaireId);
        }
        // 保存问题
        // 对象转化
        List<Question> questions = new ArrayList<>();
        for (QuestionVo dto : questionsDto) {
            Question question = new Question();
            BeanUtil.copyProperties(dto, question);
            questions.add(question);
        }
        // 保存，如果失败返回错误
        try{
            for(Question question : questions) {
                questionMapper.createQuestion(question);
            }
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400, "保存问题失败"));
        }

        // 根据问卷id查询刚才保存好的问卷问题,为options的questionId赋值
        List<Question> questionsBySave = getQuestionsBySort(questionnaireId);
        for (int i = 0; i < questionsDto.size(); i++) {
            // 如果是单选或者多选，才设置选项的问题id
            if (questionsDto.get(i).getType() != 3) {
                for (int j = 0; j < questionsDto.get(i).getOption().size(); j++) {
                    questionsDto.get(i).getOption().get(j)
                            .setQuestionId(questionsBySave.get(i).getId());
                }
            }
        }
        // 保存选项
        List<Option> optionsToSave = new ArrayList<>();
        for (QuestionVo dto : questionsDto) {
            // 排除客观题
            if (dto.getType() != 3) {
                optionsToSave.addAll(dto.getOption());
            }
        }
        if (!optionsToSave.isEmpty()) {
            for(Option option : optionsToSave) {
                optionMapper.createOption(option);
            }

        }

        // 更改问卷状态，并向控制器返回结果
        return ResponseUtil.build(Result.ok());
    }

    public List<QuestionVo> getSingleQuestions(Integer questionnaireId) {
        // 获取所有
        List<QuestionVo> questions = getQuestions(questionnaireId);
        // 排除多选和客观题
        questions.removeIf(questionVo -> questionVo.getType() == 3
                || questionVo.getType() == 2);

        return questions;
    }


    public List<QuestionVo> getQuestions(Integer questionnaireId) {
        // 查询问卷问题列表
        List<Question> rawQuestions = questionMapper.getQuestionsByQuestionnaireId(questionnaireId);
        List<QuestionVo> questions = new ArrayList<>();
        for (Question question : rawQuestions){
            List<Option> options = optionMapper.getOptionByQuestionId(question.getId());
            questions.add(new QuestionVo(question, options));
        }

        // 处理由于连接查询出现的笛卡尔积情况造成重复列表数据
        // 通过LinkedHashSet去除重复数据列，并且保留插入顺序
        LinkedHashSet<QuestionVo> questionVoHashSet = new LinkedHashSet<>(questions);

        // 转为List并按照问题顺序字段排序
        questions = new ArrayList<>(questionVoHashSet);
        // 排序(按照问题顺序升序排序)，由于连接查询结果客观题是在集合最后的，所以还是需要排序(排除客观题不在后面的情况)
        CollUtil.sort(questions, Comparator.comparing(Question::getSort));

//        log.info("问题列表：" + questions);
        // 设置问题type的value值
        return questions;
    }



    private List<Question> getQuestionsBySort(Integer questionnaireId) {
        List<Question> questions=questionMapper.getQuestionByQuestionnaireId(questionnaireId);
        CollUtil.sort(questions, Comparator.comparing(Question::getSort));
        return questions;
    }


    private void deleteQuestions(Integer questionnaireId) {
        List<Question> questions = questionMapper.getQuestionByQuestionnaireId(questionnaireId);
        for(Question question : questions) {
            questionMapper.deleteQuestionById(question.getId());
        }
    }

    public ResponseEntity<Result> deleteQuestion(Integer questionId) {
        try {
            Question question=questionMapper.getQuestionById(questionId);
            Questionnaire questionnaire=questionnaireMapper.getQuestionnaireById(question.getQuestionnaireId());
            if(questionnaire.getStatus()!=0){
                return ResponseUtil.build(Result.error(400,"问卷已发布，不能更改"));
            }
            questionMapper.deleteQuestionById(questionId);
            return ResponseUtil.build(Result.ok());
        }catch(Exception e){
            return ResponseUtil.build(Result.error(400,"删除失败"));
        }

    }
}
