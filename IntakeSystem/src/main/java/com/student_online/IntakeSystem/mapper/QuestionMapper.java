package com.student_online.IntakeSystem.mapper;


import com.student_online.IntakeSystem.model.po.Option;
import com.student_online.IntakeSystem.model.po.Question;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("select * from question where questionnaire_id=#{questionnaireId}")
    List<Question> getQuestionsByQuestionnaireId(int questionnaireId);
    
    @Select("select * from option where question_id=#{questionId}")
    List<Option> getOptionsByQuestionId(int questionId);

    @Insert("insert into question (questionnaire_id,sort,content,type) values (#{questionnaireId},#{sort},#{content},#{type})")
    void createQuestion(Question question);

    @Delete("delete from question where id=#{id}")
    void deleteQuestionById(int id);

    @Select("select * from question where id=#{id}")
    Question getQuestionById(int id);

    @Select("select * from question where sort=#{sort}")
    List<Question> getQuestionBySort(int sort);

    @Select("select * from question where questionnaire_id=#{QuestionnaireId}")
    List<Question> getQuestionByQuestionnaireId(int QuestionnaireId);


    @Update("update question set questionnaire_id=#{questionnaireId},sort=#{sort},content=#{content},type=#{type} where id=#{id}")
    void updateQuestion(Question question);

}
