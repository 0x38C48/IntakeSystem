package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScreenMapper {
    
    @Select("SELECT * FROM user where " +
            "name like #{name} and " +
            "username like #{studentNumber} and " +
            "gender like #{gender} and " +
            "college like #{college} and " +
            "major like #{major} and " +
            "station like #{station} and " +
            "department like #{department} and " +
            "tag like #{tag} " +
            "order by #{order} " +
            "LIMIT #{size}" +
            "offset #{offset}"
            )
    List<User> get(@Param("name") String name,
                   @Param("studentNumber") String studentNumber,
                   @Param("gender") String gender,
                   @Param("college") String college,
                   @Param("major") String major,
                   @Param("station") String station,
                   @Param("department") String department,
                   @Param("tag") String tag,
                   @Param("order") String order,
                   @Param("offset") Integer offset,
                   @Param("size") Integer size);
}
