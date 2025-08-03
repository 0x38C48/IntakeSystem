package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.mapper.UserMapper;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.constant.REDIS;
import com.student_online.IntakeSystem.model.po.User;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.BCryptUtil;
import com.student_online.IntakeSystem.utils.JwtUtil;
import com.student_online.IntakeSystem.utils.LoginUtils.BkzhjxLogin;
import com.student_online.IntakeSystem.utils.LoginUtils.SduLogin;
import com.student_online.IntakeSystem.utils.LoginUtils.ServicedeskLogin;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @SneakyThrows
    public Result loginCas(String studentNumber, String password, String captcha) {
        SduLogin sduLogin = new SduLogin(studentNumber, password);
        if(captcha!= null && !captcha.isEmpty()){
            sduLogin.captcha = captcha;
            sduLogin.JSESSIONID = REDIS.r.opsForHash().get("jsessionid", studentNumber).toString();
            sduLogin.cookie_adx = REDIS.r.opsForHash().get("cookie-adx", studentNumber).toString();
        }

        String ticket = sduLogin.login(ServicedeskLogin.GATE_WAY);
        
        if((captcha==null || captcha.isEmpty()) && Objects.equals(ticket,"need captcha")) {
            REDIS.r.opsForHash().put("jsessionid", studentNumber, sduLogin.JSESSIONID);
            REDIS.r.opsForHash().put("cookie-adx", studentNumber, sduLogin.cookie_adx);
            return Result.error(400, "需要验证码");
        }
        
        String cookie = ServicedeskLogin.fetchServicedeskCookie(ticket);
        
        Map<String, String> userInfo = ServicedeskLogin.fetchStudentInfo(cookie, studentNumber);
        
        if (!MAPPER.user.isUsernameExists(studentNumber)) {
            User user = new User(1, studentNumber, null, userInfo.get("USER_SEX"), userInfo.get("UNIT_NAME"), userInfo.get("MAJOR_NAME"), userInfo.get("USER_NAME"), userInfo.get("EMAIL"), 0);
            MAPPER.user.insertUser(user);
        }
        
        String token = JwtUtil.generate(studentNumber);
        
        return Result.success(token, "登入成功");
    }
    
    public Result login(String studentNumber, String password) {
        if(!MAPPER.user.isUsernameExists(studentNumber)){
            return Result.error(400, "用户不存在");
        }
        String hsPassword = MAPPER.user.getPasswordByUsername(studentNumber);
        if (!BCryptUtil.checkpw(password, hsPassword)) {
            return Result.error(400, "密码错误");
        }
        String token = JwtUtil.generate(studentNumber);
        return Result.success(token, "登入成功");
    }
    
    public Result updatePassword(String oldPassword, String newPassword) {
        String studentNumber = ThreadLocalUtil.get().studentNumber;
        if(MAPPER.user.getPasswordByUsername(studentNumber) != null && !BCryptUtil.checkpw(oldPassword, MAPPER.user.getPasswordByUsername(studentNumber))){
            return Result.error(400, "旧密码错误");
        }
        MAPPER.user.updatePasswordByUsername(studentNumber, BCryptUtil.hashpw(newPassword));
        return Result.ok();
    }
}
