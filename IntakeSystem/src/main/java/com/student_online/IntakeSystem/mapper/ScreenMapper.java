package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface ScreenMapper {
    
    @Select("SELECT u.name,u.username,u.gender,u.college,u.major,t.value as tag ,d.name as depart,d.id as departId, f.questionnaire_id,f.id as finishId FROM user u " +
            "JOIN finish f " +
            "ON f.uid = u.uid " +
            "JOIN department d ON " +
            "d.id = (select department_id from questionnaire where id = f.questionnaire_id) " +
            "LEFT JOIN tag t ON " +
            "t.uid = u.uid and " +
            "t.depart_id = (select department_id from questionnaire where id = f.questionnaire_id) " +
            
            
            "where " +
            "u.name like #{name} and " +
            "u.username like #{studentNumber} and " +
            "u.gender like #{gender} and " +
            "u.college like #{college} and " +
            "u.major like #{major} and " +
            "((select station_id from department where id = d.id) in ${stations}) or #{stations} = '(-1)' and " +
            "(t.value like #{tag} or #{tag} = '%') and " +
            "(d.id = #{department} or #{department} = -1) " +
            
            "order by ${orderBy} ${order} " +
            "LIMIT #{size} " +
            "offset #{offset}"
            )
    List<Map<String, Object>> get(@Param("name") String name,
                    @Param("studentNumber") String studentNumber,
                    @Param("gender") String gender,
                    @Param("college") String college,
                    @Param("major") String major,
                    @Param("department") Integer department,
                    @Param("tag") String tag,
                    @Param("order") String order,
                    @Param("orderBy") String orderBy,
                    @Param("stations") String stations,
                    @Param("offset") Integer offset,
                    @Param("size") Integer size);
    
    @Select("SELECT * FROM user WHERE username like #{username} and name like #{name}")
    List<User> getUser(String username, String name);
}
