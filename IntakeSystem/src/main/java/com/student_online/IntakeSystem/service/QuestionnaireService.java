package com.student_online.IntakeSystem.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;


import com.student_online.IntakeSystem.model.vo.QuestionVo;
import com.student_online.IntakeSystem.model.vo.QuestionnaireAndQuestionVo;
import com.student_online.IntakeSystem.model.po.Questionnaire;
import com.student_online.IntakeSystem.mapper.QuestionnaireMapper;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 调查问卷表（主要是问卷的基本信息） 服务实现类
 * </p>
 *
 * @author nekotaku
 * @since 2023-12-13
 */
@Service
@Slf4j
public class QuestionnaireService {

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @Autowired
    private QuestionService questionService;
    
    public ResponseEntity<Result> saveOrUpdateQuestionnaire(Questionnaire questionnaire,int departmentId) {
        try {
            log.info("添加或更新问卷表基本信息服务");
            // 标题去空格
            questionnaire.setTitle(questionnaire.getTitle().trim());

            // 根据departmentId

            // 是否更新操作，排除自己重复情况
            if (questionnaireMapper.getQuestionnaireByDepartmentId(departmentId) != null) {

                // 更新操作时，判断问卷限制数量是否修改，如果修改且小于当前已收集的问卷数量，则不能修改
                questionnaireMapper.updateQuestionnaire(questionnaire);
                return ResponseUtil.build(Result.ok());
            }
            // 保存
            questionnaireMapper.createQuestionnaire(questionnaire);
            return ResponseUtil.build(Result.ok());
        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "编辑问卷失败"));
        }
    }
    
    
    
    public ResponseEntity<Result> listQuestionnairesForDepartment(int departmentId) {
        try{
            Questionnaire questionnaire=questionnaireMapper.getQuestionnaireByDepartmentId(departmentId);
            if(questionnaire==null){
                return ResponseUtil.build(Result.error(404,"该部门未设置问卷"));
            }else return ResponseUtil.build(Result.success(questionnaire,"返回该部门问卷信息"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "编辑问卷失败"));
        }
    }

    public ResponseEntity<Result>changeStatus(Integer questionnaireId) {
        Questionnaire questionnaire = questionnaireMapper.getQuestionnaireById(questionnaireId);
        if(questionnaire!=null){
            int status = questionnaire.getStatus();
            questionnaire.setStatus(1-status);
            questionnaireMapper.updateQuestionnaire(questionnaire);
            return ResponseUtil.build(Result.ok());
        }
        else return ResponseUtil.build(Result.error(400,"状态码提供有误"));

    }

    public ResponseEntity<Result> getQuestionnaireById(int questionnaireId) {
        try{
            Questionnaire questionnaire=questionnaireMapper.getQuestionnaireByDepartmentId(questionnaireId);
            if(questionnaire==null){
                return ResponseUtil.build(Result.error(404,"未找到"));
            }else return ResponseUtil.build(Result.success(questionnaire,"返回该问卷信息"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取问卷失败"));
        }
    }


    
    public ResponseEntity<Result> getQuestionnaireDetailedByDepartmentId(int departmentId) {
        Questionnaire questionnaire1=questionnaireMapper.getQuestionnaireByDepartmentId(departmentId);
        if(questionnaire1==null){
            return ResponseUtil.build(Result.error(404,"该部门无问卷"));
        }else {

            int questionnaireId=questionnaire1.getId();
            List<QuestionVo> questions = questionService.getQuestions(questionnaireId);

            // 获取问卷基本信息
            Questionnaire questionnaireById = questionnaireMapper.getQuestionnaireById(questionnaireId);

            QuestionnaireAndQuestionVo questionnaire = new QuestionnaireAndQuestionVo();

            // 设置问题
            questionnaire.setQuestions(questions);

            // 使用拷贝
            BeanUtil.copyProperties(questionnaireById, questionnaire);

            return ResponseUtil.build(Result.success(questionnaire, "返回问卷和题目"));
        }
    }


    public ResponseEntity<Result> updateQuestionnaireCollected(Questionnaire questionnaire) {
        // 检查问卷是否已经达到结束时间
        boolean isEnd = (boolean) checkQuestionnaireIsEnd(questionnaire).getBody().getData();
        if (isEnd) {
            // 已经结束了无法提交
            return ResponseUtil.build(Result.error(400,"已经结束了无法提交"));
        }
        // 设置数量+1
        questionnaire.setCollected(questionnaire.getCollected() + 1);

        // 更新问卷收集数量字段
        questionnaireMapper.updateQuestionnaire(questionnaire);


        checkQuestionnaireIsEnd(questionnaire);

        return ResponseUtil.build(Result.ok());
    }

    
    @Transactional
    public ResponseEntity<Result> publishQuestionnaire(Integer questionnaireId) {
        Questionnaire questionnaire = questionnaireMapper.getQuestionnaireById(questionnaireId);
        // 检验问卷状态,如果不是未发布或未开始或审核通过直接返回错误信息
        if (questionnaire.getStatus() != 0) {
            return ResponseUtil.build(Result.error(400,"该问卷已发布"));
        }
        // 时间检验
        LocalDateTime questionnaireStartTime = questionnaire.getStartTime();
        LocalDateTime questionnaireEndTime = questionnaire.getEndTime();
        // (比较结束时间是否大于开始时间五分钟)
        Duration diff = LocalDateTimeUtil.between(questionnaireStartTime, questionnaireEndTime);
        if (diff.toMinutes() < 5) {
            return ResponseUtil.build(Result.error(400,"结束时间距离起始时间太近"));
        }
        // 检查开始时间是否大于等于系统时间(开始时间不能小于系统时间，考虑延迟设置15s)
        LocalDateTime now = LocalDateTimeUtil.now();
        if (LocalDateTimeUtil.between(questionnaireStartTime, now).getSeconds() >= 15) {
            return ResponseUtil.build(Result.error(400,"开始时间距现在太近"));
        }

        // 更新问卷状态(变成发布状态)
        // 如果时间已经到了，变成发布中状态
        if (LocalDateTimeUtil.between(LocalDateTimeUtil.now(), questionnaireStartTime).getSeconds() <= 0) {
            return changeStatus(questionnaire.getId());

        } else return ResponseUtil.build(Result.error(400,"当前不在问卷起止时间内"));
    }


    public ResponseEntity<Result> changeQuestionnaireTime(int questionnaireId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("更新问卷状态服务");
        Questionnaire questionnaire = questionnaireMapper.getQuestionnaireById(questionnaireId);
        if (questionnaire != null) {
            questionnaire.setStartTime(startTime);
            questionnaire.setEndTime(endTime);
            try{
                questionnaireMapper.updateQuestionnaire(questionnaire);
                return ResponseUtil.build(Result.ok());
            }catch(Exception e){
                return ResponseUtil.build(Result.error(400,"更新失败"));
            }
        }
        else return ResponseUtil.build(Result.error(404,"未找到该问卷"));
    }


    
    public ResponseEntity<Result> getQuestionnairesByStatus(Integer status) {
        List<Questionnaire> questionnaires=questionnaireMapper.getQuestionnaireByStatus(status);
        return ResponseUtil.build(Result.success(questionnaires,"获取成功"));
    }


    
    public ResponseEntity<Result> checkQuestionnaireIsStart(Questionnaire questionnaire) {
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime questionnaireStartTime = questionnaire.getStartTime();
        LocalDateTime questionnaireEndTime = questionnaire.getEndTime();
        return ResponseUtil.build(Result.success(currentTime.isAfter(questionnaireStartTime) && currentTime.isBefore(questionnaireEndTime),"获取开始状态成功"));
    }


    public ResponseEntity<Result> checkQuestionnaireIsEnd(Questionnaire questionnaire) {
        try {
            // 获取当前时间
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime questionnaireEndTime = questionnaire.getEndTime();
            // 如果当前时间大于结束时间，则将问卷状态设置为已结束
            if (currentTime.isAfter(questionnaireEndTime)) {
                // 更新问卷状态
                changeStatus(questionnaire.getId());
                return ResponseUtil.build(Result.success(true,"获取结束状态成功"));
            }

            return ResponseUtil.build(Result.success(false,"获取结束状态成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取结束状态失败"));
        }
    }


    
    @Transactional
    public ResponseEntity<Result> deleteQuestionnaireById(int departmentId) {



        Questionnaire questionnaireById = questionnaireMapper.getQuestionnaireByDepartmentId(departmentId);
        if (BeanUtil.isNotEmpty(questionnaireById)) {
                questionnaireMapper.deleteQuestionnaireById(questionnaireById.getId());

                return ResponseUtil.build(Result.ok());

        }
        else return ResponseUtil.build(Result.error(404,"未找到该问卷"));

    }



}
