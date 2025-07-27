package com.student_online.IntakeSystem.mapper;

import com.student_online.IntakeSystem.model.po.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PermissionMapper {
    @Insert("insert into permission (uid,station_id) values (#{uid},#{stationId})")
    void createPermission(Permission permission);

    @Update("update permission set uid=#{uid},station_id=#{stationId}")
    void updatePermission(Permission permission);

    @Delete("delete from permission where id=#{id}")
    void deletePermissionById(int id);

    @Select("select * from permission where id=#{id}")
    Permission getPermissionById(int id);

    @Select("select * from permission where uid=#{uid}")
    List<Permission> getPermissionByUid(int uid);

    @Select("select * from permission where uid=#{uid} and station_id=#{stationId}")
    Permission getPermissionByUidAndStationId(int uid, int stationId);
}
