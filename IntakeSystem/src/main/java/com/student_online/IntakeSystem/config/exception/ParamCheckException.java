package com.student_online.IntakeSystem.config.exception;


import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.config.exception.CommonErrException;
import lombok.Getter;

@Getter
public class ParamCheckException extends CommonErrException {
    public ParamCheckException() {
        super(CommonErr.PARAM_WRONG);
    }
    
    public ParamCheckException(String msg) {
        super(CommonErr.PARAM_WRONG);
        ERROR.setMsg(msg);
    }
    
    @Override
    public String getMessage() {
        return "参数有误: " + super.getMessage();
    }
}

