package com.student_online.IntakeSystem.service;
import cn.idev.excel.FastExcel;
import com.student_online.IntakeSystem.IntakeSystemApplication;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.pljo.UserForExcel;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.utils.MapUtil;
import com.student_online.IntakeSystem.utils.PermissionUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import com.sun.tools.javac.Main;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
    @Resource
    ScreenService screenService;
    
    @SneakyThrows
    public ResponseEntity<StreamingResponseBody> downloadExcel(int departId) {
        Department department = MAPPER.department.getDepartmentById(departId);
        
        String executor = ThreadLocalUtil.get().studentNumber;
        int uid = MAPPER.user.getUserIdByUsername(executor);
        if(!PermissionUtil.check(uid,department.getStationId())){
            return ResponseEntity.status(403).build();
        }
        
        
        
        String fileName = department.getName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) +".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        
        
        List<UserForExcel> dataList = new ArrayList<>();
        
        List<Map<String, Object>> maps = (List<Map<String, Object>>) screenService.getWills(null,null,null,null,null,departId,null,null,null,null,null,null).getData();
        for (Map<String, Object> map : maps) {
            UserForExcel userForExcel = new UserForExcel(map);
            dataList.add(userForExcel);
        }
        
        FastExcel.write("/app/excel/" + fileName, UserForExcel.class)
                .sheet(department.getName())
                .doWrite(dataList);
        
        File file = new File("/app/excel/" + fileName);
        FileSystemResource resource = new FileSystemResource(file);
        
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + encodedFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        
        StreamingResponseBody stream = out -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            } finally {
                try {
                    Files.deleteIfExists(file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(stream);
    }
}
