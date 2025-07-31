package com.student_online.IntakeSystem.mapper;


import com.student_online.IntakeSystem.model.po.Question;
import com.student_online.IntakeSystem.model.vo.QuestionVo;
import org.apache.ibatis.annotations.*;


import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("SELECT\n" +
            "            q.id, q.questionnaire_id, q.type, q.content, q.sort,\n" +
            "            o.id, o.content, o.sort\n" +
            "        FROM question q LEFT JOIN option o ON q.id = o.id\n" +
            "        WHERE\n" +
            "            q.questionnaire_id = #{questionnaireId}\n" +
            "          AND\n" +
            "            (q.type != 3 OR q.type IS NULL)\n" +
            "        UNION\n" +
            "        SELECT\n" +
            "            q.id, q.questionnaire_id, q.type, q.content, q.sort,\n" +
            "            NULL AS option_id, NULL AS content, NULL AS sort\n" +
            "        FROM\n" +
            "            question q\n" +
            "        WHERE\n" +
            "            q.questionnaire_id = #{questionnaireId}\n" +
            "          AND\n" +
            "            q.type = 3")
    List<QuestionVo> getQuestionsAndOptionsByQuestionnaireId(int questionnaireId);

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
