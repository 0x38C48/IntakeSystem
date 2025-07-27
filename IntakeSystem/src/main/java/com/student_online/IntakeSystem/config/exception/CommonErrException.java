package com.student_online.IntakeSystem.config.exception;


import com.student_online.IntakeSystem.model.vo.Result;

public class CommonErrException extends RuntimeException implements ExceptionReturn {
    protected final CommonErr ERROR;

    public CommonErrException(CommonErr err) {
        ERROR = err;
    }

    //静态构造器
    public static CommonErrException raise(CommonErr err) {
        return new CommonErrException(err);
    }

    @Override
    public String getMessage() {
        return ERROR.getMessage();
    }

    @Override
    public Result toResult() {
        return Result.error(ERROR);
    }
}
