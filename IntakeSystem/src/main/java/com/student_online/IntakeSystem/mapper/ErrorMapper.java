package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Error;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ErrorMapper {
    
    @Insert("INSERT INTO error (content, category, stacktrace,url,username) VALUES (#{content}, #{category}, #{stacktrace}, #{url}, #{username})")
    void log(Error error);
}
