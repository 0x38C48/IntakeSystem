package com.student_online.IntakeSystem.utils;

import com.student_online.IntakeSystem.model.constant.STATIC;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * jwt工具,生成令牌和解析令牌
 */
public class JwtUtil {
    //jwt密钥解析
    public static final SecretKey KEY = Keys.hmacShaKeyFor(STATIC.VALUE.jwt_secret.getBytes());
    
    //jwt加密方式
    public static final SecureDigestAlgorithm<SecretKey,SecretKey> ALGORITHM = Jwts.SIG.HS256;
    
    //设定claim使用的键
    public static final String CLAIM_KEY = "claims";
    
    //生成token，可以包裹更多东西，这里包装一个obj
    public static String generate(String studentNumber) {
        return Jwts.builder()
                .header().add("type","JWT")
                .and()
                .claim(CLAIM_KEY, new CLAIMS(studentNumber))
                .expiration(Date.from(Instant.now().plusMillis(STATIC.VALUE.jwt_expire)))
                .signWith(KEY,ALGORITHM)
                .compact();
    }
    
    //解析token，得到包装的obj
    @SuppressWarnings("unchecked")
    public static CLAIMS getClaims(String token){
        try {
            return new CLAIMS((LinkedHashMap<String, Object>)
                    Jwts.parser()
                            .verifyWith(KEY)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .get(CLAIM_KEY));
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    //使用内部类来完成固定化的jwt操作流程
    public static class CLAIMS {
        public String studentNumber;
        
        public CLAIMS(String studentNumber) {
            this.studentNumber = studentNumber;
        }
        
        public CLAIMS(LinkedHashMap<String, Object> linkedHashMap) {
            studentNumber = (String) linkedHashMap.get("studentNumber");
        }
    }
}
