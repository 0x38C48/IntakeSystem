package com.student_online.IntakeSystem.config.exception;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.config.exception.CommonErrException;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Error;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@Slf4j
@RestControllerAdvice
public class HandlerException {
    
    public static void logError(Exception e){
        try {
            Error error = ThreadLocalUtil.getError();
            error.setContent(e.getMessage());
            error.setCategory(e.getClass().getName());
            error.setStacktrace(getStackTraceAsString(e));
            ThreadLocalUtil.setError(error);
            MAPPER.error.log(error);
        } catch (Exception ex) {
            System.out.println("log error error");
        }
    }
    
    private static String getStackTraceAsString(Exception e) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
    
    //非自定义错误抛出
    
    //Mybatis抛出数据库连接异常
    @ExceptionHandler(MyBatisSystemException.class)
    public Result error(MyBatisSystemException e) {
        System.out.println("连接到数据库异常!");
        e.printStackTrace();
        logError(e);
        return Result.error(CommonErr.CONNECT_TO_MYSQL_FAILED);
    }
    
    //spring-web抛出超出最大上传数据异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result error(MaxUploadSizeExceededException e) {
        System.out.println(e.getMessage());
        logError(e);
        return Result.error(CommonErr.FILE_OUT_OF_LIMIT);
    }
    
    //spring-http抛出http不可读异常，可能包含参数异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result error(HttpMessageNotReadableException e) {
        System.out.println(e.getMessage());
        if (e.getCause() != null && e.getCause() instanceof JsonMappingException jsonMappingException) {
            if (jsonMappingException.getCause() != null && jsonMappingException.getCause() instanceof ParamCheckException paramCheckException) {
                System.out.println(paramCheckException.getMessage());
                return paramCheckException.toResult();
            }
        }
        logError(e);
        e.printStackTrace();
        return Result.error(CommonErr.PARAM_WRONG);
    }
    
    
    //自定义错误抛出
    
    //参数传入错误异常
    @ExceptionHandler(ParamCheckException.class)
    public Result error(ParamCheckException e) {
        System.out.println(e.getMessage());
        logError(e);
        return e.toResult();
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result error(MethodArgumentTypeMismatchException e) {
        System.out.println(e.getMessage());
        logError(e);
        return Result.error(CommonErr.PARAM_WRONG);
    }
    
    //token解析异常
    @ExceptionHandler(TokenException.class)
    public Result error(TokenException e) {
        System.out.println(e.getMessage());
        logError(e);
        return e.toResult();
    }
    
    //参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result error(MethodArgumentNotValidException e) {
        System.out.println(e.getMessage());
        logError(e);
        return Result.error(CommonErr.PARAM_WRONG);
    }
    
    
    //普通错误异常
    @ExceptionHandler(CommonErrException.class)
    public Result error(CommonErrException e) {
        System.out.println(e.getMessage());
        logError(e);
        return e.toResult();
    }
    
    //其它异常抛出
    
    @ExceptionHandler(RuntimeException.class)
    public Result error(RuntimeException e) {
        e.printStackTrace();
        logError(e);
        return Result.error(String.valueOf(e),401);
    }
    
    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        logError(e);
        return Result.error("未知错误,请联系开发人员",500);
    }
    
}
