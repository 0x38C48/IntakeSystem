package com.student_online.IntakeSystem.utils;

import com.student_online.IntakeSystem.model.constant.STATIC;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jwt工具,生成令牌和解析令牌
 */
public class JwtUtil {
    //jwt密钥解析
    public static final String KEY = STATIC.VALUE.jwt_secret;
    
    //jwt加密方式
    public static final SecureDigestAlgorithm<SecretKey,SecretKey> ALGORITHM = Jwts.SIG.HS256;
    
    //设定claim使用的键
    public static final String CLAIM_KEY = "casID";
    
    private static final byte[] salt = "KOISHIKISHIKAWAIIKAWAIIKISSKISSLOVELY".getBytes(StandardCharsets.UTF_8);
    
    private static final int iterationCount = 114514;
    
    private static final Map<String, SecretKey> KEY_CACHE = new ConcurrentHashMap<>();
    
    @SneakyThrows
    private static SecretKey generateSecretKey() {
        SecretKey secretKey = KEY_CACHE.get(KEY);
        if (secretKey == null) {
            PBEKeySpec spec = new PBEKeySpec(KEY.toCharArray(), salt, iterationCount, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] secretBytes = factory.generateSecret(spec).getEncoded();
            secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
            KEY_CACHE.put(KEY, secretKey);
        }
        return secretKey;
    }
    
    
//    public static String generate(String obj) {
//        SecretKey secretKey = generateSecretKey();
//        return Jwts.builder()
//                .header().add("type","JWT")
//                .and()
//                .claim(CLAIM_KEY, obj)
//                .expiration(new Date(System.currentTimeMillis() + STATIC.VALUE.jwt_expire))
//                .signWith(secretKey,ALGORITHM)
//                .compact();
//    }
    
    //解析token，得到包装的obj
    @SuppressWarnings("unchecked")
    public static String getClaim(String token){
        SecretKey secretKey = generateSecretKey();
        try {
            return (String) Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(CLAIM_KEY);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
    
    //使用内部类来完成固定化的jwt操作流程
    public static class CLAIMS {
        public Integer uid;
        public String otherInfo;

        public CLAIMS(Integer uid, String otherInfo) {
            this.uid = uid;
            this.otherInfo = otherInfo;
        }
        
        public CLAIMS(LinkedHashMap<String, Object> linkedHashMap) {
            uid = (Integer) linkedHashMap.get("uid");
            otherInfo = (String) linkedHashMap.get("otherInfo");
        }
    }
}
