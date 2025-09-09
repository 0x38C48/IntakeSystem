package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.service.ExcelService;
import jakarta.annotation.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/excel")
public class ExcelController {
    @Resource
    private ExcelService excelService;
    
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadExcel(@RequestParam int departId) {
        return excelService.downloadExcel(departId);
    }
}
