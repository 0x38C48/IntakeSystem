package com.student_online.IntakeSystem.service;

import cn.hutool.core.collection.CollUtil;
import com.student_online.IntakeSystem.mapper.AnswerMapper;
import com.student_online.IntakeSystem.mapper.FinishMapper;
import com.student_online.IntakeSystem.mapper.OptionMapper;
import com.student_online.IntakeSystem.mapper.UserMapper;
import com.student_online.IntakeSystem.model.dto.QuestionnaireInfoDto;
import com.student_online.IntakeSystem.model.po.Answer;
import com.student_online.IntakeSystem.model.po.Finish;
import com.student_online.IntakeSystem.model.po.User;
import com.student_online.IntakeSystem.model.vo.AnswerVo;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerService {
    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private FinishMapper finishMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OptionMapper optionMapper;

    @Transactional
    public ResponseEntity<Result> saveAnswer(AnswerVo answerVo, Integer finishId) {


        SqlSession sqlSession = null;
        try{
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            AnswerMapper answerMapper = sqlSession.getMapper(AnswerMapper.class);
            // 获取选项列表
            List<Answer> answerList = answerVo.getAnswerList();
            for (Answer answer : answerList) {
                // 设置问卷id
                answer.setQuestionnaireId(answerVo.getQuestionnaireId());
                // 设置日志id
                answer.setFinishId(finishId);
                // 保存答案
                answerMapper.createAnswer(answer);
            }
            sqlSession.commit();
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            if (sqlSession != null) {
                // 显式回滚事务
                sqlSession.rollback();
            }
            return ResponseUtil.build(Result.error(400,"保存回答失败"));
        }finally {
            // 确保 sqlSession 总是被关闭
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Transactional
    public ResponseEntity<Result> editAnswer(AnswerVo answerVo, Integer finishId) {


        SqlSession sqlSession = null;
        try{
            sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            AnswerMapper answerMapper = sqlSession.getMapper(AnswerMapper.class);
            // 获取选项列表
            List<Answer> answerList = answerVo.getAnswerList();
            for (Answer answer : answerList) {
                // 设置问卷id
                answer.setQuestionnaireId(answerVo.getQuestionnaireId());
                // 设置日志id
                answer.setFinishId(finishId);
                // 编辑答案
                answerMapper.updateAnswer(answer);
            }
            sqlSession.commit();
            return ResponseUtil.build(Result.ok());
        }catch (Exception e){
            if (sqlSession != null) {
                // 显式回滚事务
                sqlSession.rollback();
            }
            return ResponseUtil.build(Result.error(400,"编辑回答失败"));
        }finally {
            // 确保 sqlSession 总是被关闭
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    
    public ResponseEntity<Result> getAnswerByFinishId(Integer id) {
        List<Answer> answerList = answerMapper.getAnswerByFinishId(id);
        if (CollUtil.isNotEmpty(answerList)) {
            return ResponseUtil.build(Result.success(answerList,"获取回答列表成功"));
        }
        else return ResponseUtil.build(Result.error(404,"找不到回答列表"));
    }


    public List<QuestionnaireInfoDto> dataCollect(Integer questionnaireId) {
        List<QuestionnaireInfoDto> dataList = new ArrayList<>();
        List<Finish>finishes=finishMapper.getFinishByQuestionnaireId(questionnaireId);
        for (Finish finish : finishes) {
            QuestionnaireInfoDto questionnaireInfoDto = new QuestionnaireInfoDto();
            User user=userMapper.getUserByUid(finish.getUid());
            questionnaireInfoDto.setUsername(user.getUsername());
            questionnaireInfoDto.setName(user.getName());
            questionnaireInfoDto.setUpdateTime(finish.getUpdateTime());
            List<Answer> answerList=answerMapper.getAnswerByFinishId(finish.getId());
            List<String> answerContents=new ArrayList<>();
            for (Answer answer : answerList) {
                String answerContent=answer.getAnswerContent()!=null?(answer.getAnswerContent()):(optionMapper.getContentById(answer.getOptionId()));
                answerContents.add(answerContent);
            }
            questionnaireInfoDto.setAnswerContent(answerContents);
            dataList.add(questionnaireInfoDto);
        }
        return dataList;
    }


}
