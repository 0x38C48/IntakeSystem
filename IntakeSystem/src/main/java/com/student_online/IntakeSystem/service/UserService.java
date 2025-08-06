package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.mapper.UserMapper;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.constant.REDIS;
import com.student_online.IntakeSystem.model.dto.UserDto;
import com.student_online.IntakeSystem.model.po.User;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.model.vo.UserVo;
import com.student_online.IntakeSystem.utils.BCryptUtil;
import com.student_online.IntakeSystem.utils.JwtUtil;
import com.student_online.IntakeSystem.utils.LoginUtils.BkzhjxLogin;
import com.student_online.IntakeSystem.utils.LoginUtils.SduLogin;
import com.student_online.IntakeSystem.utils.LoginUtils.ServicedeskLogin;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class UserService {
    
    @Value("${avatar.path.upload}")
    private String uploadPath;
    
    @Value("${server.port}")
    private String serverPort;
    
    @Value("${server.domain}")
    private String serverDomain;
    
    @Value("${avatar.path.access}")
    private String accessPath;
    
    @Value("${spring.profiles.active}")
    private String env;
    
    public Result uploadAvatar(MultipartFile file) throws IOException {
        String username = ThreadLocalUtil.get().studentNumber;
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("头像为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("上传文件非是图片");
        }
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        int userId = MAPPER.user.getUserIdByUsername(username);
        String fileName = "avatar_" + userId;
        
        
        Path directoryPath = Paths.get(uploadPath);
        try (Stream<Path> stream = Files.list(directoryPath)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(fileName))
                    .forEach(path -> {
                        path.toFile().delete();
//                        System.out.println("删除文件: " + path.getFileName());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Path path = Paths.get(uploadPath);
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
        
        File dest = new File(uploadPath+ "/" + fileName + suffix);
        file.transferTo(dest);
        
        MAPPER.user.setAvatarUrl(userId, "http"+("dev".equals(env) ? "" : "s") +  "://" + serverDomain  + ("dev".equals(env) ? ":" + serverPort : "") + accessPath + fileName + suffix);
        
        return Result.success("http"+("dev".equals(env) ? "" : "s") +  "://"  + serverDomain + ("dev".equals(env) ? ":" + serverPort : "") + accessPath + fileName + suffix,
                "头像上传成功");
    }
    
    
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
            User user = new User(1, studentNumber, null, userInfo.get("USER_SEX"), userInfo.get("UNIT_NAME"), userInfo.get("MAJOR_NAME"), userInfo.get("USER_NAME"), userInfo.get("EMAIL"), 0,null,null,"该用户很神秘,没有简介");
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
    
    public Result getUserInfo(String username) {
        String executor = ThreadLocalUtil.get().studentNumber;
        if(username != null && !username.isEmpty()) {
            if (!MAPPER.user.isUsernameExists(username)) {
                return Result.error(CommonErr.NO_DATA);
            }
            
            if (!Objects.equals(username, executor) && MAPPER.user.getTypeByUsername(executor) < 1) {
                return Result.error(CommonErr.NO_AUTHORITY);
            }
        }
        if(username == null || username.isEmpty()){
            username = executor;
        }
        
        User user = MAPPER.user.getUserByUsername(username);
        UserVo userVo = new UserVo(user);
        
        return Result.success(userVo, "获取成功");
    }
    
    public Result updateUserInfo(UserDto userDto) {
        String executor = ThreadLocalUtil.get().studentNumber;
        userDto.setUsername(executor);
        
        MAPPER.user.updateUserInfo(userDto);
        
        return Result.ok();
    }
}
