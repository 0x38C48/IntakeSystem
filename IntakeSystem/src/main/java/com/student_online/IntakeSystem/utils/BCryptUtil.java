package com.student_online.IntakeSystem.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 加密工具类
 */
public class BCryptUtil {
    public static String hashpw(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkpw(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
