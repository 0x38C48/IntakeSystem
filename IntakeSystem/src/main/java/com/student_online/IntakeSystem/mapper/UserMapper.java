package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT * from user where uid = #{uid}")
    User getUserByUid(int uid);
    
    @Select("SELECT * from user where username = #{username}")
    User getUserByUsername(String username);
    
    @Insert("INSERT INTO user(username, password, studentNum, type) VALUES(#{username}, #{password}, #{studentNum}, #{type})")
    void insertUser(User user);
    
    @Delete("DELETE FROM user WHERE uid = #{uid}")
    void deleteUserByUid(int uid);
    
    @Delete("DELETE FROM user WHERE username = #{username}")
    void deleteUserByUsername(String username);
    
    @Update("UPDATE user SET type = #{type} WHERE uid = #{uid}")
    void updateUserTypeByUid(@Param("uid") int uid,@Param("type") int type);
}
