package com.student_online.IntakeSystem.controller;

import com.student_online.IntakeSystem.model.constant.STATIC;
import com.student_online.IntakeSystem.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @ResponseBody
    @RequestMapping("/cas/login")
    public String casLogin(@RequestParam("token") String token, HttpServletResponse response) {
        if(token == null){
            return "无效的token";
        }
        String casId = JwtUtil.getClaim(token);
        if(casId == null){
            return "无效的token";
        }
        response.sendRedirect(STATIC.VALUE.FRONTEND_INDEX_URL + "?casId=" + casId);
        return null;
    }

}
