package com.student_online.IntakeSystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.student_online.IntakeSystem.model.po.Option;
import com.student_online.IntakeSystem.model.po.Option;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OptionMapper {
    @Insert("insert into `option` (question_id,sort,content) values (#{question_id},#{sort},#{content})")
    void createOption(Option option);

    @Delete("delete from `option` where id=#{id}")
    void deleteOptionById(int id);

    @Select("select * from `option` where id=#{id}")
    Option getOptionById(int id);

    @Update("update `option` set question_id=#{questionId},sort=#{sort},content=#{content} where id=#{id}")
    void updateOption(Option option);

}
