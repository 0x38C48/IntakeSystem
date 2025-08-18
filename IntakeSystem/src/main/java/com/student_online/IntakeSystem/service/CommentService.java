package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.CommentMapper;
import com.student_online.IntakeSystem.mapper.FinishMapper;
import com.student_online.IntakeSystem.mapper.PermissionMapper;
import com.student_online.IntakeSystem.mapper.QuestionnaireMapper;
import com.student_online.IntakeSystem.model.po.Comment;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Finish;
import com.student_online.IntakeSystem.model.po.Questionnaire;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FinishMapper finishMapper;
    @Autowired
    private FinishService finishService;
    @Autowired
    private PermissionService permissionService;


    public ResponseEntity<Result> createComment(Comment comment) {
        int finishId=comment.getFinishId();
        int uid=comment.getUid();
        int stationId=finishService.getStationIdForFinish(finishId);
        Comment comment1=commentMapper.getCommentByUidAndFinishId(uid,finishId);
        if(comment1!=null){
            return  ResponseUtil.build(Result.error(409,"您已对该问卷发表评论"));
        }
        if(permissionService.isPermitted(stationId,uid)){
            commentMapper.createComment(comment);
            return ResponseUtil.build(Result.ok());
        }
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    public ResponseEntity<Result> updateComment(Comment comment) {
        int finishId=comment.getFinishId();
        int uid=comment.getUid();
        int stationId=finishService.getStationIdForFinish(finishId);
        Comment comment1=commentMapper.getCommentByUidAndFinishId(uid,finishId);
        if(comment1==null){
            return  ResponseUtil.build(Result.error(400,"不存在该评论"));
        }
        else if(permissionService.isPermitted(stationId,uid)){
            commentMapper.updateComment(comment);
            return ResponseUtil.build(Result.ok());
        }
        else return ResponseUtil.build(Result.error(401,"无权限"));
    }

    public ResponseEntity<Result> delete(int commentId,int uid) {
        try {
            Comment comment = commentMapper.getCommentById(commentId);
            int finishId = comment.getFinishId();
            int stationId = finishService.getStationIdForFinish(finishId);
            if (permissionService.isPermitted(stationId, uid)) {
                commentMapper.deleteCommentById(commentId);
                return ResponseUtil.build(Result.ok());
            } else return ResponseUtil.build(Result.error(401, "无权限"));
        }catch (Exception e){
            return ResponseUtil.build(Result.error(400, "删除失败"));
        }
    }

    public ResponseEntity<Result> getCommentByCommentId(int commentId) {
        try {
            Comment comment = commentMapper.getCommentById(commentId);
            if (comment == null) {
                return ResponseUtil.build(Result.error(404, "未找到该评论"));
            } else {
                return ResponseUtil.build(Result.success(comment, "返回评论"));
            }
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getCommentByFinishId(int finishId,int uid) {
        try {
            int stationId = finishService.getStationIdForFinish(finishId);
            if (permissionService.isPermitted(stationId, uid)) {
                List<Comment> comment = commentMapper.getCommentByFinishId(finishId);
                if (comment == null) {
                    return ResponseUtil.build(Result.error(404, "未找到评论"));
                } else {
                    return ResponseUtil.build(Result.success(comment, "返回所有评论"));
                }
            } else {
                List<Comment> comment = commentMapper.getViewableCommentByFinishId(finishId);
                if (comment == null) {
                    return ResponseUtil.build(Result.error(404, "未找到评论"));
                } else {
                    return ResponseUtil.build(Result.success(comment, "返回可见评论"));
                }
            }

        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }

    public ResponseEntity<Result> getCommentByUid(int uid) {
        try {
                List<Comment> comment = commentMapper.getCommentByUid(uid);
                if (comment == null) {
                    return ResponseUtil.build(Result.error(404, "未找到评论"));
                } else {
                    return ResponseUtil.build(Result.success(comment, "返回用户评论"));
                }

        }catch (Exception e) {
            return ResponseUtil.build(Result.error(400, "获取失败"));
        }
    }


}
