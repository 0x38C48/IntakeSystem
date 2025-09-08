package com.student_online.IntakeSystem.service;

import com.student_online.IntakeSystem.config.exception.CommonErr;
import com.student_online.IntakeSystem.model.constant.MAPPER;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Station;
import com.student_online.IntakeSystem.model.po.User;
import com.student_online.IntakeSystem.model.vo.Result;
import com.student_online.IntakeSystem.utils.MapUtil;
import com.student_online.IntakeSystem.utils.ThreadLocalUtil;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScreenService {
    
    public Result getWills(String name, String studentNumber, String gender, String college, String major,Integer department, String tag, String order, String orderBy, Integer stationId, Integer page, Integer size) {
        String executor = ThreadLocalUtil.get().studentNumber;
        if(MAPPER.permission.getPermissionByUid(MAPPER.user.getUserIdByUsername(executor)) == null){
            return Result.error(CommonErr.NO_AUTHORITY);
        }
        
        
        if(order == null || order.isEmpty()){
            order = "ASC";
        } else{
            order = "DESC";
        }
        if(page == null || page < 1 || size == null || size < 1){
            page = 1;
            size = 20;
        }
        if(name != null && !name.isEmpty()) {
            name = "%" + name + "%";
        }
        if(studentNumber != null && !studentNumber.isEmpty()) {
            studentNumber = "%" + studentNumber + "%";
        }
        if(tag != null && !tag.isEmpty()) {
            tag = "%" + tag + "%";
        }
        
        if(name == null || name.isEmpty()){
            name = "%";
        }
        if(studentNumber == null || studentNumber.isEmpty()){
            studentNumber = "%";
        }
        if(gender == null || gender.isEmpty()){
            gender = "%";
        }
        if(college == null || college.isEmpty()){
            college = "%";
        }
        if(major == null || major.isEmpty()){
            major = "%";
        }
        if(department == null){
            department = -1;
        }
        if(tag == null || tag.isEmpty()){
            tag = "%";
        }
        if(orderBy == null || orderBy.isEmpty()){
            orderBy = "u.name";
        }
        
        switch (orderBy) {
            case "studentNumber":
                orderBy = "u.username";
                break;
            case "gender":
                orderBy = "u.gender";
                break;
            case "college":
                orderBy = "u.college";
                break;
            case "major":
                orderBy = "u.major";
                break;
            case "department":
                orderBy = "d.name";
                break;
            case "tag":
                orderBy = "t.value";
                break;
            default:
                orderBy = "u.name";
                break;
        }
        
        String stations = "";
        if(stationId == null){
            stations = "(-1)";
        } else {
            List<Integer> stationIds = getStations(stationId);
            for (int i = 0; i < stationIds.size(); i++) {
                if (i == 0) {
                    stations = "("+stationIds.get(i);
                } else if(i < stationIds.size()-1) {
                    stations += ","+stationIds.get(i);
                } else {
                    stations += ","+stationIds.get(i)+")";
                }
            }
        }
        System.out.println("stations: "+stations);
        
        try {
            return Result.success(MAPPER.screen.get(name, studentNumber, gender, college, major, department, tag, order, orderBy, stations , (page-1)*size, size),"获取成功");
        } catch (BadSqlGrammarException e) {
            System.out.println(e.getSql());
            e.printStackTrace();
            return Result.error(CommonErr.PARAM_WRONG);
        }
    }
    
    private void process(List<Integer> open,List<Integer> result){
        for (int i = 0;true;i++) {
            int stationId = open.get(i);
            List<Station> list = MAPPER.station.getChildren(stationId);
            if (list.isEmpty()) {
                result.add(stationId);
            } else {
                for (Station station : list) {
                    open.add(station.getId());
                }
            }
            if(i >= open.size()-1){
                break;
            }
        }
    }
    
    private List<Integer> getStations(Integer stationId){
        List<Integer> open = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        open.add(stationId);
        
        process(open,result);
        
        return result;
    }
    
    public Result getUser(String username, String name) {
        String executor = ThreadLocalUtil.get().studentNumber;
        if(MAPPER.permission.getPermissionByUid(MAPPER.user.getUserIdByUsername(executor)) == null){
            return Result.error(CommonErr.NO_AUTHORITY);
        }
        
        if(name == null || name.isEmpty()){
            name = "%";
        } else {
            name = "%" + name + "%";
        }
        if(username == null || username.isEmpty()){
            username = "%";
        } else {
            username = "%" + username + "%";
        }
        
        List<User> users = MAPPER.screen.getUser(username, name);
        List<Map<String, Object>> list = MapUtil.transToListMap(users);
        for (Map<String, Object> map : list) {
            map.remove("password");
            map.remove("type");
            map.remove("uid");
            map.remove("create_time");
        }
        
        return Result.success(list,"获取成功");
        
    }
}
