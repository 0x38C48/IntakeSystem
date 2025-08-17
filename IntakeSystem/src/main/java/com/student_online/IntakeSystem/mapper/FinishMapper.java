package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Finish;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FinishMapper {
    @Insert("insert into finish (questionnaire_id,uid) values (#{questionnaire_id},#{uid})")
    void createFinish(Finish finish);

    @Delete("delete from finish where questionnaire_id=#{questionnaireId}")
    void deleteFinishById(int questionnaireId);

    @Select("select * from finish where uid=#{uid} and questionnaire_id=#{questionnaireId}")
    Finish getFinishByUidAndQuestionnaireId(@Param("uid") int uid,@Param("questionnaireId")int questionnaireId);

    @Select("select * from finish where questionnaire_id=#{QuestionnaireId}")
    List<Finish> getFinishByQuestionnaireId(int QuestionnaireId);

    @Select("select * from finish where uid=#{uid}")
    List<Finish> getFinishByUid(int uid);

    @Select("select * from finish where id=#{id}")
    Finish getFinishById(int id);
    
    @Update("update finish set questionnaire_id=#{questionnaireId},uid=#{uid} where id=#{id}")
    void updateFinish(Finish finish);
}
