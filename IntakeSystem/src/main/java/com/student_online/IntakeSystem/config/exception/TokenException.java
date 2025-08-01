package com.student_online.IntakeSystem.config.exception;

import lombok.Getter;


@Getter
public class TokenException extends CommonErrException {
    public TokenException() {
        super(CommonErr.TOKEN_CHECK_FAILED);
    }
    
    public TokenException(String msg) {
        super(CommonErr.TOKEN_CHECK_FAILED);
        ERROR.setMsg(msg);
    }
    
    @Override
    public String getMessage() {
        return "An exceptions occurred because token from front-end is wrong: " + super.getMessage();
    }
}
