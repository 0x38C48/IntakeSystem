package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Answer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnswerMapper {
    @Insert("insert into answer (questionnaire_id,answer_content,，finish_id,option_id,type,question_id) values (#{questionnaireId},#{answerContent},#{finishId},#{optionId},#{type},#{questionId})")
    void createAnswer(Answer answer);

    @Delete("delete from answer where question_id=#{id}")
    void deleteAnswerById(int id);

    @Select("select * from answer where id=#{id}")
    Answer getAnswerById(int id);

    @Select("select * from answer where questionnaire_id=#{QuestionnaireId}")
    List<Answer> getAnswerByQuestionnaireId(int QuestionnaireId);

    @Select("select * from answer where finish_id=#{finishId}")
    List<Answer> getAnswerByFinishId(int finishId);


    @Update("update answer set questionnaire_id=#{questionnaireId},answer_content=#{answerContent},option_id=#{optionId},type=#{type} ,question_id=#{questionId} where id=#{id}")
    void updateAnswer(Answer answer);


}
