package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Comment;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.service.CommentService;
import com.student_online.IntakeSystem.service.DepartmentService;
import com.student_online.IntakeSystem.utils.ResponseUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/create")
    public ResponseEntity<Result> createComment(@RequestBody Comment comment) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        comment.setUid(Integer.parseInt(uid));
//        if(Integer.parseInt(uid)!=comment.getUid())return ResponseUtil.build(Result.error(400,"uid设置有误"));
        return commentService.createComment(comment);
    }

    @PostMapping("/update")
    public ResponseEntity<Result> updateComment(@RequestBody Comment comment) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        comment.setUid(Integer.parseInt(uid));
//        if(Integer.parseInt(uid)!=comment.getUid())return ResponseUtil.build(Result.error(400,"uid设置有误"));
        return commentService.updateComment(comment);
    }

    @PostMapping("/delete")
    public ResponseEntity<Result> delete(@RequestParam int id) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return commentService.delete(id,Integer.parseInt(uid));
    }

    @GetMapping("/view/user")
    public ResponseEntity<Result> getCommentByUsername(@RequestParam String username) {
        int uid = MAPPER.user.getUserIdByUsername(username);
        
        return commentService.getCommentByUid(uid);
    }

    @GetMapping("/view/id")
    public ResponseEntity<Result> getCommentById(@RequestParam int id) {
        return commentService.getCommentByCommentId(id);
    }

    @GetMapping("/view/finish")
    public ResponseEntity<Result> getCommentByFinishId(@RequestParam int finishId) {
        String username = ThreadLocalUtil.get().studentNumber;
        String uid = MAPPER.user.getUserIdByUsername(username) + "";
        return commentService.getCommentByFinishId(finishId,Integer.parseInt(uid));
    }





}
