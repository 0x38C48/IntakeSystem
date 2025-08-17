package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Tag;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    @Resource
    private PermissionService permissionService;
    
    public Result create(String username, String tag, int departId) {
        int executor = MAPPER.user.getUserIdByUsername(ThreadLocalUtil.get().studentNumber);
        if(!permissionService.isPermitted(departId, executor)){
            return Result.error(CommonErr.NO_AUTHORITY);
        }
        
        int uid = MAPPER.user.getUserIdByUsername(username);
        MAPPER.tag.create(uid, tag, departId);
        return Result.ok();
    }
    
    public Result view(String username, int departId) {
        int executor = MAPPER.user.getUserIdByUsername(ThreadLocalUtil.get().studentNumber);
        if(!permissionService.isPermitted(departId, executor)){
            return Result.error(CommonErr.NO_AUTHORITY);
        }
        
        int uid = MAPPER.user.getUserIdByUsername(username);
        Tag tag = MAPPER.tag.get(uid, departId);
        return Result.success(tag, "获取标签成功");
    }
    
    public Result delete(int tagId) {
        Tag tag = MAPPER.tag.getById(tagId);
        
        int executor = MAPPER.user.getUserIdByUsername(ThreadLocalUtil.get().studentNumber);
        if(!permissionService.isPermitted(tag.getDepartId(), executor)){
            return Result.error(CommonErr.NO_AUTHORITY);
        }
        
        MAPPER.tag.delete(tagId);
        return Result.ok();
    }
}
