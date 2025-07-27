package com.student_online.IntakeSystem.utils;


import com.student_online.IntakeSystem.model.vo.Result;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<Result> build(Result result) {
        return ResponseEntity
                .status(result.httpStatus())
                .body(result);
    }
}
