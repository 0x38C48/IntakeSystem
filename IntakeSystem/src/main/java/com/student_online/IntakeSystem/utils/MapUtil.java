package com.student_online.IntakeSystem.utils;

import com.student_online.IntakeSystem.model.constant.MAPPER;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MapUtil {
    public static Map<String, Object> transToMap(Object obj){
        Map<String, Object> map = new HashMap<>();
        
        Method[] getters = obj.getClass().getMethods();
        for (Method getter : getters) {
            if(! getter.getName().startsWith("get") || getter.getParameterCount() > 0 || "getClass".equals(getter.getName())){
                continue;
            }
            try {
                try {
                    String key = getter.getName().substring(3);
                    key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                    map.put(key, getter.invoke(obj));
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return map;
    }
    
    public static <T> List<Map<String, Object>> transToListMap(List<T> list){
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (Object obj : list) {
            listMap.add(transToMap(obj));
        }
        return listMap;
    }
    
    @SneakyThrows
    public static Map<String, Object> transToMapWithUsername(Object obj){
        Map<String, Object> map = transToMap(obj);
        Method method = obj.getClass().getMethod("getUid");
        try {
            map.put("username", MAPPER.user.getUsernameById((Integer) method.invoke(obj)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
