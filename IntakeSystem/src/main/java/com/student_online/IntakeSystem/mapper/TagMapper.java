package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Tag;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface TagMapper {
    
    @Insert("INSERT INTO tag (uid, value, depart_id) VALUES (#{uid}, #{value}, #{departId})")
    void create(@Param("uid") int uid,@Param("value") String tag,@Param("departId") int departId);
    
    @Select("SELECT * FROM tag WHERE uid = #{uid} AND depart_id = #{departId}")
    Tag get(@Param("uid") int uid,@Param("departId") int departId);
    
    @Select("SELECT * FROM tag WHERE id = #{tagId}")
    Tag getById(int tagId);
    
    @Delete("DELETE FROM tag WHERE id = #{tagId}")
    void delete(int tagId);
}
