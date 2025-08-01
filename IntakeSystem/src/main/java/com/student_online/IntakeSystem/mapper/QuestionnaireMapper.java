package com.student_online.IntakeSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.student_online.IntakeSystem.model.po.Questionnaire;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionnaireMapper  {
    @Insert("insert into questionnaire (start_time,end_time,title,status,description) values (#{startTime},#{endTime},#{title},#{status},#{description})")
    void createQuestionnaire(Questionnaire questionnaire);

    @Delete("delete from questionnaire where id=#{id}")
    void deleteQuestionnaireById(int id);

    @Select("select * from questionnaire where id=#{id}")
    Questionnaire getQuestionnaireById(int id);

    @Update("update questionnaire set start_time=#{startTime},end_time=#{endTime},title=#{title},status=#{status},description=#{description} where id=#{id}")
    void updateQuestionnaire(Questionnaire questionnaire);

}
