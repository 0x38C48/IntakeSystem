package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Option;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OptionMapper {
    @Insert("insert into `option` (question_id,option_sort,option_content) values (#{questionId},#{optionSort},#{optionContent})")
    void createOption(Option option);

    @Delete("delete from `option` where question_id=#{id}")
    void deleteOptionByQuestionId(int id);

    @Delete("delete from `option` where option_id=#{id}")
    void deleteOptionById(int id);

    @Select("select * from `option` where question_id=#{questionId}")
    List<Option> getOptionByQuestionId(int questionId);

    @Select("select option_content from `option` where option_id=#{optionId}")
    String getContentById(int optionId);

    @Update("update `option` set question_id=#{questionId},option_sort=#{optionSort},option_content=#{optionContent} where option_id=#{optionId}")
    void updateOption(Option option);

}