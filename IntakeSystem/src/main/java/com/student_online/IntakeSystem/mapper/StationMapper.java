package com.student_online.IntakeSystem.mapper;
import com.student_online.IntakeSystem.model.po.Station;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StationMapper {
    @Insert("insert into station (name,p_id,description,is_department) values (#{name},#{pId},#{description},#{isDepartment})")
    void createStation(Station station);

    @Select("select * from station where id=#{id}")
    Station getStationById(int id);
    
    @Select("select * from station where p_id = #{id}")
    List<Station> getChildren(Integer id);

    @Select("select * from station where name=#{name}")
    Station getStationByName(String name);

    @Select("select * from station where name like '%${name}%'")
    List<Station> getStationsByName(String name);

    @Select("select * from station where p_id=#{pid}")
    List<Station> getStationByParentId(int pid);

    @Delete("delete from station where id=#{id}")
    void deleteStationById(int id);

    @Update("update station set name=#{name},p_id=#{p_id},description=#{description} ,is_department=#{isDepartment} where id=#{stationId}")
    void updateStation(Station station);
    
    @Select("select * from station where p_id IS NULL or p_id = 0")
    List<Station> getRoots();
    
    @Select("select p_id from station where id=#{id}")
    int getPidById(Integer pId);
}
