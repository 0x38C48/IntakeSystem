package com.student_online.IntakeSystem.mapper;
import com.student_online.IntakeSystem.model.po.Department;
import com.student_online.IntakeSystem.model.po.Station;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    @Insert("insert into department (name,station_id,p_id,description) values (#{name},#{stationId},#{pId},#{description}")
    void createDepartment(Department department);

    @Select("select * from department where id=#{id}")
    Department getDepartmentById(int id);

    @Select("select * from department where name=#{name}")
    Department getDepartmentByName(String name);

    @Select("select * from department where name like '%${name}%' ")
    List<Department> getDepartmentsByName(String name);

    @Delete("delete from department where id=#{id}")
    void deleteDepartmentById(int id);

    @Update("update department set name=#{name},p_id=#{pId},description=#{description} where id=#{id}")
    void updateDepartment(Department department);
    
    @Select("select * from department where station_id=#{stationId}")
    Department getDepartmentByStationId(Integer stationId);
    
    @Update("update department set image=#{s} where id=#{id}")
    void setImg(int id, String s);
}
