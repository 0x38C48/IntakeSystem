package com.student_online.IntakeSystem.utils;

import com.student_online.IntakeSystem.model.po.Error;

public class ThreadLocalUtil {
    private static final ThreadLocal<JwtUtil.CLAIMS> THREADLOCAL = new ThreadLocal<>();
    
    private static final ThreadLocal<Error> ERROR_THREADLOCAL = new ThreadLocal<>();
    
    public static void setError(Error error) {
        ERROR_THREADLOCAL.set(error);
    }
    
    public static Error getError() {
        return ERROR_THREADLOCAL.get();
    }

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
