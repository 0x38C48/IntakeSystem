package com.student_online.IntakeSystem.mapper;
import com.student_online.IntakeSystem.model.po.Department;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    @Insert("insert into department (name,station_id,p_id,description,image) values (#{name},#{stationId},#{pId},#{description},#{image})")
    void createDepartment(Department department);

    @Select("select * from department where id=#{id}")
    Department getDepartmentById(int id);

    @Select("select * from department where name=#{name}")
    Department getDepartmentByName(String name);

    @Delete("delete from department where id=#{id}")
    void deleteDepartmentById(int id);

    @Update("update department set name=#{name},p_id=#{p_id},description=#{description} where id=#{id}")
    void updateDepartment(Department department);
}
