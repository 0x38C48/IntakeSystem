package com.student_online.IntakeSystem.config.exception;


/*
 * 常用错误返回
 *
 * */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum CommonErr {
    
    //------------------------------------------------访问控制------------------------------------------------//
    
    REQUEST_NOT_ALLOW(10000, "当前条件或时间不允许〒▽〒"),
    
    REQUEST_FREQUENTLY(10001, "请求繁忙，请稍后再试"),
    
    METHOD_NOT_ALLOW(10002, "方法不允许"),
    
    NO_AUTHORITY(10003,"你不能访问这里哦!"),
    
    OPERATE_REPEAT(10004,"重复的操作"),
    
    //------------------------------------------------连接异常------------------------------------------------//
    
    NETWORK_WRONG(20000, "网络错误"),
    
    CONNECT_TO_MYSQL_FAILED(20001,"数据获取异常了惹T_T"),
    
    //------------------------------------------------检查控制------------------------------------------------//
    
    PARAM_WRONG(30000, "参数范围或格式错误"),
    
    TOKEN_CHECK_FAILED(30001,"token检查有误"),
    
    SQL_NOT_ALLOWED_IN_STRING(30002,"包含不允许的字符!"),
    
    //------------------------------------------------数据异常------------------------------------------------//
    
    RESOURCE_NOT_FOUND(40000, "你要找的东西好像走丢啦X﹏X"),
    
    RESOURCE_IS_DELETE(40001,"你寻找的资源好像已经被删除了呢"),
    
    NO_DATA(40002,"没有找到结果哦QwQ"),
    
    THIS_IS_LAST_PAGE(40003, "这是最后一页，再怎么找也没有啦"),
    
    THIS_IS_FIRST_PAGE(40004, "没有上一页啦"),
    
    //------------------------------------------------文件流控制------------------------------------------------//
    
    FILE_FORMAT_ERROR(50000,"文件格式错误"),
    
    FILE_OUT_OF_LIMIT(50001,"文件大小超出限制!"),
    
    FILE_OPERATOR_ERR(50002,"文件操作失败!"),
    
    //------------------------------------------------其他----------//
    
    //------------------------------------------------选课操作错误----------//
    
    SELECTION_EXISTS(1001, "选课记录已存在"),
    SELECTION_NOT_FOUND(1002, "选课记录不存在"),
    STUDENT_NOT_FOUND(1003, "学生不存在"),
    COURSE_NOT_FOUND(1004, "课程不存在"),
    INVALID_STUDENT_NUMBER(1005, "学号格式错误，应为12位数字"),
    CLAZZ_NOT_FOUND(1006, "班级不存在");
    
    
    private int code;
    private String message;
    
    public CommonErr setMsg(String msg) {
        message = msg;
        return this;
    }
    CommonErr(int errorCode, String errorMessage) {
        code = errorCode;
        message = errorMessage;
    }
    
    public String getMessage() {
        return message;
    }
    public Integer getCode(){
        return code;
    }
}
