package com.student_online.IntakeSystem.utils;

import com.student_online.IntakeSystem.model.constant.MAPPER;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    public static Map<String, Object> transToMap(Object obj){
        Map<String, Object> map = new HashMap<>();
        
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return map;
    }
    
    public static Map<String, Object> transToMapWithUsername(Object obj){
        Map<String, Object> map = transToMap(obj);
        try {
            Field field = obj.getClass().getField("uid");
            field.setAccessible(true);
            try {
                map.put("username", MAPPER.user.getUsernameById((Integer) field.get(obj)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
