package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Option;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OptionMapper {
    @Insert("insert into `option` (question_id,option_sort,option_content) values (#{question_id},#{sort},#{content})")
    void createOption(Option option);

    @Delete("delete from `option` where option_id=#{id}")
    void deleteOptionById(int id);

    @Select("select * from `option` where question_id=#{questionnaireId}")
    List<Option> getOptionByQuestionnaireId(int questionnaireId);

    @Update("update `option` set question_id=#{questionId},option_sort=#{sort},option_content=#{content} where option_id=#{id}")
    void updateOption(Option option);

}
