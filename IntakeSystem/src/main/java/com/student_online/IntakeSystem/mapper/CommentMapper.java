package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Insert("insert into comment (content,finish_id,viewable,uid) values (#{content},#{finishId},#{viewable},#{uid})")
    void createComment(Comment comment);

    @Select("select * from comment where id=#{id}")
    Comment getCommentById(int id);

    @Select("select * from comment where finish_id=#{finishId}")
    List<Comment> getCommentByFinishId(Integer finishId);

    @Select("select * from comment where uid=#{uid}")
    List<Comment> getCommentByUid(Integer uid);

    @Select("select * from comment where uid=#{uid} and finish_id=#{finishId}")
    Comment getCommentByUidAndFinishId(@Param("uid") int uid,@Param("finishId")int finishId);

    @Delete("delete from comment where id=#{id}")
    void deleteCommentById(int id);

    @Update("update comment set content=#{content},finish_id=#{finishId},viewable=#{viewable},uid=#{uid} where id=#{id}")
    void updateComment(Comment comment);
}
