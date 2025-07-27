package com.student_online.IntakeSystem.config.exception;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.config.exception.CommonErrException;
import com.student_online.IntakeSystem.model.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


@Slf4j
@RestControllerAdvice
public class HandlerException {
    
    //非自定义错误抛出
    
    //Mybatis抛出数据库连接异常
    @ExceptionHandler(MyBatisSystemException.class)
    public Result error(MyBatisSystemException e) {
        System.out.println("连接到数据库异常!");
        e.printStackTrace();
        return Result.error(CommonErr.CONNECT_TO_MYSQL_FAILED);
    }
    
    //spring-web抛出超出最大上传数据异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result error(MaxUploadSizeExceededException e) {
        System.out.println(e.getMessage());
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
        e.printStackTrace();
        return Result.error(CommonErr.PARAM_WRONG);
    }
    
    
    //自定义错误抛出
    
    //参数传入错误异常
    @ExceptionHandler(ParamCheckException.class)
    public Result error(ParamCheckException e) {
        System.out.println(e.getMessage());
        return e.toResult();
    }
    
    //token解析异常
    @ExceptionHandler(TokenException.class)
    public Result error(TokenException e) {
        System.out.println(e.getMessage());
        return e.toResult();
    }
    
    //参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result error(MethodArgumentNotValidException e) {
        System.out.println(e.getMessage());
        return Result.error(CommonErr.PARAM_WRONG);
    }
    
    
    //普通错误异常
    @ExceptionHandler(CommonErrException.class)
    public Result error(CommonErrException e) {
        System.out.println(e.getMessage());
        return e.toResult();
    }
    
    //其它异常抛出
    
    @ExceptionHandler(RuntimeException.class)
    public Result error(RuntimeException e) {
        e.printStackTrace();
        return Result.error(String.valueOf(e),401);
    }
    
    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.error(String.valueOf(e),402);
    }
    
}
