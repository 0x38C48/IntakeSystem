package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.dto.UserDto;
import com.student_online.IntakeSystem.model.po.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT EXISTS(SELECT 1 FROM user WHERE username = #{username})")
    boolean isUsernameExists(String username);
    
//    @Select("SELECT castgc_cookies FROM user WHERE username = #{username}")
//    String getCastgcCookiesByUsername(String username);
    
    @Select("SELECT * from user where uid = #{uid}")
    User getUserByUid(int uid);
    
    @Select("SELECT * from user where username = #{username}")
    User getUserByUsername(String username);
    
    @Select("SELECT password FROM user WHERE username = #{username}")
    String getPasswordByUsername(String username);
    
    @Insert("INSERT INTO user(username, password, type, gender, college, major, name, email) VALUES(#{username}, #{password}, #{type},#{gender}, #{college}, #{major}, #{name}, #{email})")
    void insertUser(User user);
    
    @Delete("DELETE FROM user WHERE uid = #{uid}")
    void deleteUserByUid(int uid);
    
    @Delete("DELETE FROM user WHERE username = #{username}")
    void deleteUserByUsername(String username);
    
    @Update("UPDATE user SET type = #{type} WHERE uid = #{uid}")
    void updateUserTypeByUid(@Param("uid") int uid,@Param("type") int type);
    
    @Update("UPDATE user SET password = #{hashpw} WHERE username = #{studentNumber}")
    void updatePasswordByUsername(@Param("studentNumber") String studentNumber,@Param("hashpw") String hashpw);
    
    @Select("SELECT uid from user WHERE username = #{username}")
    int getUserIdByUsername(String username);
    
    @Update("UPDATE user SET avatar = #{fileName} WHERE uid = #{userId}")
    void setAvatarUrl(@Param("userId") int userId,@Param("fileName") String fileName);
    
    @Select("SELECT avatar FROM user WHERE username = #{username}")
    String getAvatar(String username);
    
    @Select("SELECT type FROM user WHERE username = #{executor}")
    int getTypeByUsername(String executor);
    
    @Update("UPDATE user SET qq = #{qq}, profile = #{profile},email = #{email} WHERE username = #{username}")
    void updateUserInfo(UserDto userDto);
    
    @Select("SELECT username FROM user WHERE uid = #{id}")
    String getUsernameById(Integer id);
}
