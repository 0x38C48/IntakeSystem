package com.student_online.IntakeSystem.utils;

public class ThreadLocalUtil {
    private static final ThreadLocal<JwtUtil.CLAIMS> THREADLOCAL = new ThreadLocal<>();

    public static void set(JwtUtil.CLAIMS value) {
        THREADLOCAL.set(value);
    }

    public static JwtUtil.CLAIMS get() {
        return THREADLOCAL.get();
    }

    public static void remove() {
        THREADLOCAL.remove();
    }
}
